package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class GraphicalPlayerTest extends Application {

    public static void main(String[] args) { launch(args); }
    
    private void setState(GraphicalPlayer player) {

        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);

        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        GraphicalPlayer p = new GraphicalPlayer(PLAYER_1, playerNames);
        setState(p);

        ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRoute =
                new SimpleObjectProperty<>(Stages9and10tests::claimRoute);

        ActionHandlers.ChooseTicketsHandler chooseTicketsH = (t) -> p.receiveInfo(String.format
                (StringsFr.DREW_TICKETS, playerNames.get(PLAYER_1), t.size(), StringsFr.plural(t.size())) + "\n");

        ActionHandlers.DrawTicketsHandler drawTicketsH =
                () -> p.receiveInfo("Je tire des billets ! \n");
        ActionHandlers.DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s ! \n", s));
        ActionHandlers.ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.toString();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s \n", rn, cs));
                };

        p.startTurn(drawTicketsH, drawCardH, claimRoute.get());
    //    p.chooseTickets(SortedBag.of(ChMap.tickets().subList(0, 5)), chooseTicketsH);
    //    p.chooseClaimCards(List.of(SortedBag.of(Card.RED), SortedBag.of(Card.WHITE)), cards -> {});
    }

}
