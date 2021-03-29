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

        GameState game = GameState.initial(tickets, rng);

        for(PlayerId p : PlayerId.ALL) {
            players.get(p).receiveInfo(info.get(game.currentPlayerId()).willPlayFirst());
        }

        for(PlayerId p : PlayerId.ALL) {
            players.get(p).setInitialTicketChoice(game.topTickets(Constants.INITIAL_TICKETS_COUNT));
        }

        for(PlayerId p : PlayerId.ALL) {
            SortedBag<Ticket> keptTickets = players.get(p).chooseInitialTickets();
            game = game.withInitiallyChosenTickets(p, keptTickets);
            for(PlayerId otherPlayer : PlayerId.ALL) {
                players.get(otherPlayer).receiveInfo(info.get(p).keptTickets(keptTickets.size()));

            }
        }

        do{

            PlayerId currentPlayerId = game.currentPlayerId();
            Player currentPlayer = players.get(currentPlayerId);

            Player.TurnKind action = currentPlayer.nextTurn();

            if(action.equals(Player.TurnKind.DRAW_TICKETS)) {

                SortedBag<Ticket> keptTickets = currentPlayer.chooseTickets(game.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                game = game.withChosenAdditionalTickets(game.topTickets(Constants.IN_GAME_TICKETS_COUNT), keptTickets);

                for(PlayerId p : PlayerId.ALL) {
                    players.get(p).receiveInfo(info.get(currentPlayerId).keptTickets(keptTickets.size()));

                }



            } else if(action.equals(Player.TurnKind.DRAW_CARDS)) {

                for(int i = 0; i < 2; ++i) {

                    int drawSlot = currentPlayer.drawSlot();

                    if(drawSlot >= 0 && drawSlot < Constants.FACE_UP_CARDS_COUNT) {
                        game = game.withDrawnFaceUpCard(drawSlot);
                    } else {
                        game = game.withBlindlyDrawnCard();
                    }
                }



            } else if(action.equals(Player.TurnKind.CLAIM_ROUTES)) {

                Route routeToClaim = currentPlayer.claimedRoute();
                SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                SortedBag<Card> topCards = SortedBag.of(1, game.topCard(), 1, game.withoutTopCard().topCard()).union(SortedBag.of(game.withoutTopCard().withoutTopCard().topCard()));
                SortedBag<Card> additionalCards = SortedBag.of();
                boolean hasToAddMoreCards = false;

                if(routeToClaim.level().equals(Route.Level.UNDERGROUND)) {

                    for(PlayerId p : PlayerId.ALL) {
                        players.get(p).receiveInfo(info.get(currentPlayerId).attemptsTunnelClaim(routeToClaim, initialClaimCards));
                    }

                    if(routeToClaim.additionalClaimCardsCount(initialClaimCards, topCards) > 0) {
                        additionalCards = currentPlayer.chooseAdditionalCards(game.currentPlayerState().possibleAdditionalCards(routeToClaim.
                                additionalClaimCardsCount(initialClaimCards, topCards), initialClaimCards, topCards));
                        hasToAddMoreCards = true;

                        }
                    }

                     if(hasToAddMoreCards && additionalCards.isEmpty()) {
                        for(PlayerId p : PlayerId.ALL) {
                            players.get(p).receiveInfo(info.get(currentPlayerId).didNotClaimRoute(routeToClaim));
                         }
                    } else {
                        game = game.withClaimedRoute(routeToClaim, initialClaimCards.union(additionalCards));
                         for(PlayerId p : PlayerId.ALL) {
                              players.get(p).receiveInfo(info.get(currentPlayerId).claimedRoute(routeToClaim, initialClaimCards.union(additionalCards)));
                         }
                    }
                }


            //TODO: finish this method (implement all receive infos + updateState)





        } while(!game.lastTurnBegins());



    }







}
