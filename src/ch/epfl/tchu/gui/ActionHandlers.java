package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Public interface that contains five nested functional interfaces representing different "action managers"
 * @author Karlis Velins (325180)
 */
public interface ActionHandlers {

    /**
     * FunctionalInterface with a method that is called when the player wishes to draw tickets
     */
    @FunctionalInterface
    interface DrawTicketsHandler   {
        void onDrawTickets();
    }

    /**
     * FunctionalInterface with a method that is called when the player wishes to draw a card from the given slot
     */
    @FunctionalInterface
    interface DrawCardHandler{
        void onDrawCard(int slotNumber);
    }

    /**
     * FunctionalInterface with a method that is called
     * when the player wishes to seize the given route by means of the given (initial) cards
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * FunctionalInterface with a method that is called
     * when the player has chosen to keep the tickets given following a ticket draw
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * FunctionalInterface with a method that is called
     * when the player has chosen to use the given cards as initial or additional cards when taking possession of a route
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        void onChooseCards(SortedBag<Card> cards);
    }

}
