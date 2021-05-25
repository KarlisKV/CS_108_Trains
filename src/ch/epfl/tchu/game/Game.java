package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import java.util.*;


/**
 * Game class summarizes all of the classes and combines them into one method 'play'
 * @author Daniel Polka (326800)
 * @author Karlis Velins (325180)
 */
public final class Game {

    private Game(){}

    /**
     * The method where all the magic happens. This method calls all other methods in order for the players to actually play the game
     * @param players  HashMap mapping a player's PlayerId to Player
     * @param playerNames HashMap mapping a player's PlayerId to the player's name
     * @param tickets SortedBag containing the Tickets used for the game
     * @param rng Random used for the game
     * @throws IllegalArgumentException if given amount of players are not equal to the default setting and also the length of the player
     * names are equal to the set player count
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {

        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);

        //Initialising players and info map

        Map<PlayerId, Info> info = new HashMap<>();

        for(PlayerId p : PlayerId.ALL) {
            players.get(p).initPlayers(p, playerNames);
            info.put(p, new Info(playerNames.get(p)));
        }

        //Beginning of game

        GameState game = GameState.initial(tickets, rng);

        allPlayersReceiveInfo(players, info.get(game.currentPlayerId()).willPlayFirst());


        for(PlayerId p : PlayerId.ALL) {

            allPlayersUpdateState(players, game);

            players.get(p).setInitialTicketChoice(game.topTickets(Constants.INITIAL_TICKETS_COUNT));
            game = game.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        for(PlayerId p : PlayerId.ALL) {

            allPlayersUpdateState(players, game);

            SortedBag<Ticket> keptTickets = players.get(p).chooseInitialTickets();
            game = game.withInitiallyChosenTickets(p, keptTickets);
            allPlayersReceiveInfo(players, info.get(p).keptTickets(keptTickets.size()));
        }



        //Mid-game and 2 last turns

        boolean end = false;
        boolean lastTurnsBegin = false;
        int lastTurns = 0;

        do{


            PlayerId currentPlayerId = game.currentPlayerId();
            Player currentPlayer = players.get(currentPlayerId);

            if(lastTurnsBegin) {
                ++lastTurns;
                if(lastTurns == 2) {
                    end = true;
                }
            }

            allPlayersReceiveInfo(players, info.get(currentPlayerId).canPlay());

            allPlayersUpdateState(players, game);

            Player.TurnKind action = currentPlayer.nextTurn();

            allPlayersUpdateState(players, game);

            switch(action) {

                case DRAW_CARDS:

                    for(int i = 0; i < 2; ++i) {

                        int drawSlot = currentPlayer.drawSlot();

                         if(drawSlot == Constants.DECK_SLOT) {

                             game = game.withCardsDeckRecreatedIfNeeded(rng);
                             game = game.withBlindlyDrawnCard();
                             allPlayersReceiveInfo(players, info.get(currentPlayerId).drewBlindCard());

                         } else {

                             game = game.withCardsDeckRecreatedIfNeeded(rng);
                             game = game.withDrawnFaceUpCard(drawSlot);
                             allPlayersReceiveInfo(players, info.get(currentPlayerId).drewVisibleCard(game.cardState().faceUpCards().get(drawSlot)));

                         }

                        allPlayersUpdateState(players, game);

                    }

                    break;


                case CLAIM_ROUTE:

                    Route routeToClaim = currentPlayer.claimedRoute();

                    boolean routeIsClaimable = true;

                    for(Route r : game.claimedRoutes()){
                        if(r.stations().equals(routeToClaim.stations())) {
                            routeIsClaimable = false;
                            break;
                        }
                    }

                    if(game.playerState(currentPlayerId).canClaimRoute(routeToClaim) && routeIsClaimable) {

                        allPlayersUpdateState(players, game);

                        SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                        game = game.withCardsDeckRecreatedIfNeeded(rng);

                        SortedBag<Card> additionalCards = SortedBag.of();
                        boolean hasToAddMoreCards = false;

                        if(routeToClaim.level().equals(Route.Level.UNDERGROUND)) {

                            allPlayersReceiveInfo(players, info.get(currentPlayerId).attemptsTunnelClaim(routeToClaim, initialClaimCards));

                            SortedBag<Card> topCards = SortedBag.of();

                            if(game.cardState().deckSize() < Constants.ADDITIONAL_TUNNEL_CARDS) {
                                while(!game.cardState().isDeckEmpty()){
                                    //Just in case to avoid bugs, but shouldn't happen
                                    game = game.withCardsDeckRecreatedIfNeeded(rng);
                                    game = game.withMoreDiscardedCards(SortedBag.of(game.topCard()));
                                    game = game.withoutTopCard();
                                }
                                game = game.withCardsDeckRecreatedIfNeeded(rng);

                                allPlayersUpdateState(players, game);

                            }


                            for(int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {

                                //Just in case to avoid bugs, but shouldn't happen
                                game = game.withCardsDeckRecreatedIfNeeded(rng);

                                topCards = topCards.union(SortedBag.of(game.topCard()));
                                game = game.withoutTopCard();
                            }

                            allPlayersUpdateState(players, game);

                            if(routeToClaim.additionalClaimCardsCount(initialClaimCards, topCards) > 0) {

                                if((game.currentPlayerState().possibleAdditionalCards(routeToClaim.
                                        additionalClaimCardsCount(initialClaimCards, topCards), initialClaimCards)).size() > 0) {
                                    additionalCards = currentPlayer.chooseAdditionalCards(game.currentPlayerState().possibleAdditionalCards(routeToClaim.
                                            additionalClaimCardsCount(initialClaimCards, topCards), initialClaimCards));
                                }
                                hasToAddMoreCards = true;
                            }


                            game = game.withMoreDiscardedCards(topCards);

                        }

                        allPlayersUpdateState(players, game);

                        if(hasToAddMoreCards && additionalCards.isEmpty()) {
                            allPlayersReceiveInfo(players, info.get(currentPlayerId).didNotClaimRoute(routeToClaim));
                        } else {
                            game = game.withClaimedRoute(routeToClaim, initialClaimCards.union(additionalCards));
                            allPlayersReceiveInfo(players, info.get(currentPlayerId).claimedRoute(routeToClaim, initialClaimCards.union(additionalCards)));
                            allPlayersUpdateState(players, game);


                        }

                    }

                    break;




                case DRAW_TICKETS:

                    SortedBag<Ticket> keptTickets;

                    if(game.ticketsCount() < Constants.IN_GAME_TICKETS_COUNT) {
                        keptTickets = currentPlayer.chooseTickets(game.topTickets(game.ticketsCount()));
                        game = game.withChosenAdditionalTickets(game.topTickets(game.ticketsCount()), keptTickets);
                    } else {
                        keptTickets = currentPlayer.chooseTickets(game.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                        game = game.withChosenAdditionalTickets(game.topTickets(Constants.IN_GAME_TICKETS_COUNT), keptTickets);
                    }

                    allPlayersReceiveInfo(players, info.get(currentPlayerId).keptTickets(keptTickets.size()));

                    break;



                default:
                    throw new IllegalArgumentException();
            }

            allPlayersUpdateState(players, game);

            if(game.lastTurnBegins() && !lastTurnsBegin) {
                allPlayersReceiveInfo(players, info.get(currentPlayerId).lastTurnBegins(game.currentPlayerState().carCount()));
                lastTurnsBegin = true;
            }

            if(!end){
                game = game.forNextTurn();
            }

        } while(!end);

        //End of game points and announcements

        Map<PlayerId, Integer> finalPoints = new HashMap<>();
        Map<PlayerId, Trail> longestTrail = new HashMap<>();
        Trail longest = Trail.longest(List.of());
        PlayerId longestTrailPlayerId = PlayerId.PLAYER_1;
        boolean equalTrailLength = false;

        for(PlayerId p: PlayerId.ALL) {
            Trail pTrail = Trail.longest(game.playerState(p).routes());
            finalPoints.put(p, game.playerState(p).finalPoints());
            longestTrail.put(p, pTrail);
            if(longest.length() < longestTrail.get(p).length()) {
                longest = longestTrail.get(p);
                longestTrailPlayerId = p;
            } else if(longest.length() == longestTrail.get(p).length() && !longest.equals(pTrail)) {
                equalTrailLength = true;
            }
        }

        if (!equalTrailLength) {

            int pointsAfterLongestTrailBonus = finalPoints.get(longestTrailPlayerId) + Constants.LONGEST_TRAIL_BONUS_POINTS;
            finalPoints.replace(longestTrailPlayerId, pointsAfterLongestTrailBonus);

            allPlayersReceiveInfo(players, info.get(longestTrailPlayerId).getsLongestTrailBonus(longest));

        } else {

            for(PlayerId p : PlayerId.ALL) {
                int pointsAfterLongestTrailBonus = finalPoints.get(p) + Constants.LONGEST_TRAIL_BONUS_POINTS;
                finalPoints.replace(p, pointsAfterLongestTrailBonus);

                allPlayersReceiveInfo(players, info.get(p).getsLongestTrailBonus(longest));
            }

        }

        int maxPoints = 0;
        int minPoints = finalPoints.get(PlayerId.PLAYER_1);
        boolean draw = false;
        PlayerId winningPlayerId = PlayerId.PLAYER_1;
        List<String> names = new ArrayList<>();

        for(PlayerId p: PlayerId.ALL) {
            if(maxPoints < finalPoints.get(p)) {
                maxPoints = finalPoints.get(p);
                draw = false;
                winningPlayerId = p;
            } else if(maxPoints == finalPoints.get(p)) {
                draw = true;
            }

            if(minPoints > finalPoints.get(p)) {
                minPoints = finalPoints.get(p);
            }

            names.add(playerNames.get(p));
        }

        if(!draw) {
            allPlayersReceiveInfo(players, info.get(winningPlayerId).won(maxPoints, minPoints));
        } else {
            allPlayersReceiveInfo(players, Info.draw(names, minPoints));
        }



    }






    /**
     * Method which is called whenever there is information to announce, and announces it to
     * both players at once to avoid code duplication
     * @param players  HashMap mapping a player's PlayerId to Player
     * @param info the String of information each player is going to receive
     */
    private static void allPlayersReceiveInfo(Map<PlayerId, Player> players, String info) {
        for(PlayerId p : PlayerId.ALL) {
            players.get(p).receiveInfo(info);
        }
    }

    /**
     * Method which is called whenever the status of the game has changed to update both player's state
     * at the same time and avoid code duplication
     * @param players  HashMap mapping a player's PlayerId to Player
     * @param newState the new public GameState
     */
    private static void allPlayersUpdateState(Map<PlayerId, Player> players, GameState newState) {

        for(Route r : newState.playerState(newState.currentPlayerId().next()).routes()) {
            newState.playerState(newState.currentPlayerId()).routes().remove(r);
        }

        for (PlayerId playerId : PlayerId.ALL) {
            players.get(playerId).updateState(newState, newState.playerState(playerId));
        }
    }


}
