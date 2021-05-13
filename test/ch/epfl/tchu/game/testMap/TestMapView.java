package ch.epfl.tchu.game.testMap;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.gui.ActionHandlers;
import ch.epfl.tchu.gui.MapViewCreator;
import ch.epfl.tchu.gui.ObservableGameState;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;

public class TestMapView extends Application {

        public static void main(String[] args) {
            Application.launch(args);
        }
        @Override
        public void start(Stage primaryStage) throws Exception {

            Scene scene = new Scene(getWagonGroup(), 200, 200);
            scene.getStylesheets().add("map.css");
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();




        }

        private static Group getWagonGroup() {
            Group g = new Group();
            g.getStyleClass().add("car");

            Rectangle r = new Rectangle();


            r.setWidth(36);
            r.setHeight(12);
            r.getStyleClass().add("filled");


            Circle circle1 = new Circle();
            circle1.setCenterX(12);
            circle1.setCenterY(6);


            Circle circle2 = new Circle();

            circle2.setCenterX(24);
            circle2.setCenterY(6);
            circle1.setRadius(3);
            circle2.setRadius(3);
            circle1.getStyleClass().add("filled");
            circle1.getStyleClass().add("filled");

            g.getChildren().add(r);
            g.getChildren().add(circle1);
            g.getChildren().add(circle2);


            return g;

        }

        @FunctionalInterface
        interface CardChooser   {
            void chooseCards (List<SortedBag<Card>> options,
                              ActionHandlers.ChooseCardsHandler handler);
        }
    }


