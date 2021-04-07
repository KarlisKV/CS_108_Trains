package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        Map<PlayerId, PlayerState> playerStates = new HashMap<>();

        allPlayersReceiveInfo(players, info.get(game.currentPlayerId()).willPlayFirst());

        for(PlayerId p : PlayerId.ALL) {
            playerStates.put(p, game.playerState(p));
            allPlayersUpdateState(players, game, playerStates);
            players.get(p).setInitialTicketChoice(game.topTickets(Constants.INITIAL_TICKETS_COUNT));
        }

        for(PlayerId p : PlayerId.ALL) {
            allPlayersUpdateState(players, game, playerStates);
            SortedBag<Ticket> keptTickets = players.get(p).chooseInitialTickets();
            game = game.withInitiallyChosenTickets(p, keptTickets);
            allPlayersReceiveInfo(players, info.get(p).keptTickets(keptTickets.size()));
        }



        //Midgame

        do{

            PlayerId currentPlayerId = game.currentPlayerId();
            Player currentPlayer = players.get(currentPlayerId);

            if(game.lastTurnBegins()) {
                allPlayersReceiveInfo(players, info.get(currentPlayerId).lastTurnBegins(game.currentPlayerState().carCount()));
            }

            allPlayersUpdateState(players, game, playerStates);

            Player.TurnKind action = currentPlayer.nextTurn();

            allPlayersUpdateState(players, game, playerStates);



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

                        allPlayersUpdateState(players, game, playerStates);

                    }

                    break;



                case CLAIM_ROUTES:

                    Route routeToClaim = currentPlayer.claimedRoute();

                    allPlayersUpdateState(players, game, playerStates);

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
                            allPlayersUpdateState(players, game, playerStates);
                        }


                        for(int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {

                            //Just in case to avoid bugs, but shouldn't happen
                            game = game.withCardsDeckRecreatedIfNeeded(rng);

                            topCards = topCards.union(SortedBag.of(game.topCard()));
                            game = game.withoutTopCard();
                        }

                        allPlayersUpdateState(players, game, playerStates);

                        if(routeToClaim.additionalClaimCardsCount(initialClaimCards, topCards) > 0) {
                            additionalCards = currentPlayer.chooseAdditionalCards(game.currentPlayerState().possibleAdditionalCards(routeToClaim.
                                    additionalClaimCardsCount(initialClaimCards, topCards), initialClaimCards, topCards));
                            hasToAddMoreCards = true;
                        }


                        game = game.withMoreDiscardedCards(topCards);

                    }

                    allPlayersUpdateState(players, game, playerStates);

                    if(hasToAddMoreCards && additionalCards.isEmpty()) {
                        allPlayersReceiveInfo(players, info.get(currentPlayerId).didNotClaimRoute(routeToClaim));
                    } else {
                        game = game.withClaimedRoute(routeToClaim, initialClaimCards.union(additionalCards));
                        allPlayersReceiveInfo(players, info.get(currentPlayerId).claimedRoute(routeToClaim, initialClaimCards.union(additionalCards)));
                        allPlayersUpdateState(players, game, playerStates);
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

            allPlayersUpdateState(players, game, playerStates);

            game = game.forNextTurn();

        } while(!game.lastTurnBegins());

        //TODO: Endgame

    }






    private static void allPlayersReceiveInfo(Map<PlayerId, Player> players, String info) {
        for(PlayerId p : PlayerId.ALL) {
            players.get(p).receiveInfo(info);
        }
    }

    private static void allPlayersUpdateState(Map<PlayerId, Player> players, PublicGameState newGameState, Map<PlayerId, PlayerState> newPlayerStates) {
        for(PlayerId p : PlayerId.ALL) {
            players.get(p).updateState(newGameState, newPlayerStates.get(p));
        }
    }

}
