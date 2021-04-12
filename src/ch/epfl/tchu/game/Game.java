package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

public final class Game {



    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {

        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);

        Map<PlayerId, Info> info = new HashMap<>();

        for(PlayerId p : PlayerId.ALL) {
            players.get(p).initPlayers(p, playerNames);
            info.put(p, new Info(playerNames.get(p)));
        }



        //Beginning of game

        GameState game = GameState.initial(tickets, rng);

        allPlayersReceiveInfo(players, info.get(game.currentPlayerId()).willPlayFirst());


        for(PlayerId p : PlayerId.ALL) {

            for (PlayerId playerId : PlayerId.ALL) {
                players.get(playerId).updateState(game, game.playerState(playerId));
            }

            players.get(p).setInitialTicketChoice(game.topTickets(Constants.INITIAL_TICKETS_COUNT));
        }

        for(PlayerId p : PlayerId.ALL) {

            for (PlayerId playerId : PlayerId.ALL) {
                players.get(playerId).updateState(game, game.playerState(playerId));
            }

            SortedBag<Ticket> keptTickets = players.get(p).chooseInitialTickets();
            game = game.withInitiallyChosenTickets(p, keptTickets);
            allPlayersReceiveInfo(players, info.get(p).keptTickets(keptTickets.size()));
        }



        //Midgame + last turn

        boolean end = false;

        int turn = 0;

        do{

            PlayerId currentPlayerId = game.currentPlayerId();
            Player currentPlayer = players.get(currentPlayerId);

            ++turn;
            System.out.println("Beginning of turn: " + turn + " | " + playerNames.get(currentPlayerId) + " | carCount: " + game.playerState(currentPlayerId).carCount() + " | cards: " + game.playerState(currentPlayerId).cards() + " | routes: " + game.playerState(currentPlayerId).routes());


            if(game.lastTurnBegins()) {
                allPlayersReceiveInfo(players, info.get(currentPlayerId).lastTurnBegins(game.currentPlayerState().carCount()));
                end = true;
            }

            for (PlayerId playerId : PlayerId.ALL) {
                players.get(playerId).updateState(game, game.playerState(playerId));
            }

            Player.TurnKind action = currentPlayer.nextTurn();

            for (PlayerId playerId : PlayerId.ALL) {
                players.get(playerId).updateState(game, game.playerState(playerId));
            }

            switch(action) {

                case DRAW_CARDS:

                    for(int i = 0; i < 2; ++i) {

                        int drawSlot = currentPlayer.drawSlot();

                        if(drawSlot >= 0 && drawSlot < Constants.FACE_UP_CARDS_COUNT) {

                            game = game.withCardsDeckRecreatedIfNeeded(rng);
                            allPlayersReceiveInfo(players, info.get(currentPlayerId).drewVisibleCard(game.cardState().faceUpCards().get(drawSlot)));
                            game = game.withDrawnFaceUpCard(drawSlot);

                        } else {

                            game = game.withCardsDeckRecreatedIfNeeded(rng);
                            game = game.withBlindlyDrawnCard();
                            allPlayersReceiveInfo(players, info.get(currentPlayerId).drewBlindCard());

                        }

                        for (PlayerId playerId : PlayerId.ALL) {
                            players.get(playerId).updateState(game, game.playerState(playerId));
                        }
                    }

                    break;



                case CLAIM_ROUTE:

                    Route routeToClaim = currentPlayer.claimedRoute();

                    boolean routeIsClaimable = true;

                    for(PlayerId p : PlayerId.ALL) {
                        for(Route r : game.playerState(p).routes()){
                            if(r.stations().equals(routeToClaim.stations())) {
                                routeIsClaimable = false;
                                break;
                            }
                        }
                    }

                    if(game.playerState(currentPlayerId).canClaimRoute(routeToClaim) && routeIsClaimable) {

                        for (PlayerId playerId : PlayerId.ALL) {
                            players.get(playerId).updateState(game, game.playerState(playerId));
                        }

                        SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                        game = game.withCardsDeckRecreatedIfNeeded(rng);

                        SortedBag<Card> additionalCards = SortedBag.of();
                        boolean hasToAddMoreCards = false;

                        if(routeToClaim.level().equals(Route.Level.UNDERGROUND)) {

                            allPlayersReceiveInfo(players, info.get(currentPlayerId).attemptsTunnelClaim(routeToClaim, initialClaimCards));

                            SortedBag<Card> topCards = SortedBag.of();

                            if(game.cardState().deckSize() < 3) {
                                while(!game.cardState().isDeckEmpty()){
                                    //Just in case to avoid bugs, but shouldn't happen
                                    game = game.withCardsDeckRecreatedIfNeeded(rng);
                                    game = game.withMoreDiscardedCards(SortedBag.of(game.topCard()));
                                }
                                game = game.withCardsDeckRecreatedIfNeeded(rng);

                                for (PlayerId playerId : PlayerId.ALL) {
                                    players.get(playerId).updateState(game, game.playerState(playerId));
                                }
                            }


                            for(int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {

                                //Just in case to avoid bugs, but shouldn't happen
                                game = game.withCardsDeckRecreatedIfNeeded(rng);

                                topCards = topCards.union(SortedBag.of(game.topCard()));
                                game = game.withoutTopCard();
                            }

                            for (PlayerId playerId : PlayerId.ALL) {
                                players.get(playerId).updateState(game, game.playerState(playerId));
                            }


                            if(routeToClaim.additionalClaimCardsCount(initialClaimCards, topCards) > 0) {

                                if((game.currentPlayerState().possibleAdditionalCards(routeToClaim.
                                        additionalClaimCardsCount(initialClaimCards, topCards), initialClaimCards, topCards)).size() > 0) {
                                    additionalCards = currentPlayer.chooseAdditionalCards(game.currentPlayerState().possibleAdditionalCards(routeToClaim.
                                            additionalClaimCardsCount(initialClaimCards, topCards), initialClaimCards, topCards));
                                }
                                hasToAddMoreCards = true;
                            }


                            game = game.withMoreDiscardedCards(topCards);

                        }

                        for (PlayerId playerId : PlayerId.ALL) {
                            players.get(playerId).updateState(game, game.playerState(playerId));
                        }


                        if(hasToAddMoreCards && additionalCards.isEmpty()) {
                            allPlayersReceiveInfo(players, info.get(currentPlayerId).didNotClaimRoute(routeToClaim));
                        } else {
                            game = game.withClaimedRoute(routeToClaim, initialClaimCards.union(additionalCards));
                            allPlayersReceiveInfo(players, info.get(currentPlayerId).claimedRoute(routeToClaim, initialClaimCards.union(additionalCards)));

                            for (PlayerId playerId : PlayerId.ALL) {
                                players.get(playerId).updateState(game, game.playerState(playerId));
                            }

                        }

                    }

                    break;




                case DRAW_TICKETS:

                    SortedBag<Ticket> keptTickets = currentPlayer.chooseTickets(game.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    game = game.withChosenAdditionalTickets(game.topTickets(Constants.IN_GAME_TICKETS_COUNT), keptTickets);

                    allPlayersReceiveInfo(players, info.get(currentPlayerId).keptTickets(keptTickets.size()));

                    break;



                default:
                    throw new IllegalArgumentException();
            }

            for (PlayerId playerId : PlayerId.ALL) {
                players.get(playerId).updateState(game, game.playerState(playerId));
            }


            if(!end){
                game = game.forNextTurn();
            }

            System.out.println("End of turn: " + turn + " | " + playerNames.get(currentPlayerId) + " | carCount: " + game.playerState(currentPlayerId).carCount() + " | cards: " + game.playerState(currentPlayerId).cards() + " | routes " + game.playerState(currentPlayerId).routes());
            System.out.println();

        } while(!end);

        //End of game points and announcements

        Map<PlayerId, Integer> finalPoints = new HashMap<>();
        Map<PlayerId, Trail> longestTrail = new HashMap<>();
        Trail longest = Trail.longest(List.of());
        PlayerId longestTrailPlayerId = PlayerId.PLAYER_1;
        boolean noBonus = false;

        for(PlayerId p: PlayerId.ALL) {
            Trail pTrail = Trail.longest(game.playerState(p).routes());
            finalPoints.put(p, game.playerState(p).finalPoints());
            longestTrail.put(p, pTrail);
            if(longest.length() < longestTrail.get(p).length()) {
                longest = longestTrail.get(p);
                longestTrailPlayerId = p;
            } else if(longest.length() == longestTrail.get(p).length() && !longest.equals(pTrail)) {
                noBonus = true;
            }
        }

        if (!noBonus) {

            int pointsAfterLongestTrailBonus = finalPoints.get(longestTrailPlayerId) + Constants.LONGEST_TRAIL_BONUS_POINTS;
            finalPoints.replace(longestTrailPlayerId, pointsAfterLongestTrailBonus);

            allPlayersReceiveInfo(players, info.get(longestTrailPlayerId).getsLongestTrailBonus(longest));

        } else {
        //    allPlayersReceiveInfo(players, "Personne n'a reçu de bonus pour le plus long chemin.");
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






    private static void allPlayersReceiveInfo(Map<PlayerId, Player> players, String info) {
        for(PlayerId p : PlayerId.ALL) {
            players.get(p).receiveInfo(info);
        }
    }


}
