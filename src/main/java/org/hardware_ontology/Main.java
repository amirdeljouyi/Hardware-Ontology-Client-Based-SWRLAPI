package org.hardware_ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();
			FXMLLoader loader = new FXMLLoader();
			File resourcesDirectory = new File("src/main/resources");
			loader.setLocation(Main.class.getResource("/Home.fxml"));
			root = (VBox) loader.load();
			Scene scene = new Scene(root);
		    
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Hardware");
			primaryStage.centerOnScreen();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}