package org.hardware_ontology.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.exceptions.LiteralException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLResultValue;

import javafx.fxml.FXML;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hardware_ontology.model.Query;

public class HomeController {
	ObservableList<String> cpuQueries = FXCollections.observableArrayList("List Of CPU brands", "Total Count Of CPU",
			"Total Count of Available CPU", "Total Count of Unavailable CPU", "List Of CPU ordered by speed",
			"Count of CPUs based on their speed", "number of cpus in different series of a brand",
			"List of cpus sorted by price");
	ObservableList<String> gpuQueries = FXCollections.observableArrayList("List Of GPU brands", "Total Count Of GPU",
			"Total Count of Available GPU", "Total Count of Unavailable GPU", "List Of CPU ordered by graphic memory",
			"List of GPUs sorted by price");
	ObservableList<String> laptopQueries = FXCollections.observableArrayList("List of Laptops sorted by price",
			"List of Laptops sorted by weight", "List of Laptops sorted by resolution",
			"List of Laptops sorted by cpu speed", "List of Laptops ram", "List of Laptops sorted internal storage",
			"List of Laptops sorted by size and weight", "Count of Laptops foreach brand",
			"Count of Laptops which have equal process power",
			"Minimum and maximum price of laptops which have equal process power");
	ObservableList<String> tabletQueries = FXCollections.observableArrayList("Count of Tablet based on OS",
			"List of Tablet ordered by resolution", "List of Tablet ordered by power",
			"List of Tablet ordered by weight", "Minimum and Maximum Price of laptops which have equal process power",
			"Minimum and Maximum Price of laptops which have equal process power");
	ObservableList<String> mobileQueries = FXCollections.observableArrayList("Count of Mobiles based on OS",
			"List of Mobiles ordered by resolution", "List of Mobiles ordered by power",
			"List of Mobiles ordered by weight", "Minimum and Maximum Price of laptops which have equal process power",
			"Minimum and Maximum Price of laptops which have equal process power", "List of Mobiles which have 2 sim");
	ObservableList<String> motherboardQueries = FXCollections.observableArrayList("Details Of Motherboards",
			"Minimum and Maximum Price of similar motherboards");
	ObservableList<String> ramQueries = FXCollections.observableArrayList("Details Of RAM",
			"Minimum and Maximum Price of similar RAM");
	ObservableList<String> multipleQueries = FXCollections.observableArrayList(
			"List of brands which produce mobile and tablet",
			"List of Mobiles and Tablets which price difference is 200",
			"Price Difference between Mobile and Tablets have similar power",
			"Count of Mobiles and Tablets and Laptops foreach price range",
			"Number of laptops weaker than foreach Mobile", "Most Expensive Laptop is Cheaper than foreach Tablet",
			"Most Cheap Tablet have similar power with foreach laptop", "Galaxy Mobiles",
			"Laptops which powerfull than all of galaxy phones and price difference is 200",
			"Alternatives for particualr phone", "Motherboards powerfull than macbooks",
			"CPUs notfound on any products of microsoft");
	private int queryIndex;
	private Query catQuery;
	private OWLOntology ontology;
	private OWLOntologyManager ontologyManager;
	private SQWRLQueryEngine queryEngine;
	private Stage stage;
	@FXML
	private Button run;
	@FXML
	private ComboBox category;
	@FXML
	private ComboBox queryBox;
	@FXML
	private AnchorPane anch;
	@FXML
	private TableView tableView;

	@FXML
	public void initialize() throws MalformedURLException, UnsupportedEncodingException {
		category.getItems().addAll("CPU", "Motherboard", "GPU", "RAM", "Tablet", "Mobile", "Laptop", "Multiple");

		try {
			// Create an OWL ontology using the OWLAPI
			ontologyManager = OWLManager.createOWLOntologyManager();
			InputStream in = getClass().getResourceAsStream("/ont/vahdati-deljouyi-electronic-device-ontology.owl");
			ontology = ontologyManager.loadOntologyFromOntologyDocument(in);
			queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
		} catch (OWLOntologyCreationException e) {
			System.err.println("Error creating OWL ontology: " + e.getMessage());
			System.exit(-1);
		} catch (RuntimeException e) {
			// System.out.println(getClass().getResource("/ont/vahdati-deljouyi-electronic-device-ontology.owl").getPath());
			System.err.println("Error starting application: " + e.getMessage());
			System.exit(-1);
		}
	}

	@FXML
	private void categoryAction() throws IOException {
		String cat = (String) category.getValue();
		queryBox.getItems().clear();
		switch (cat) {
		case "CPU":
			setToQuery(cpuQueries);
			catQuery = Query.CPU;
			break;
		case "GPU":
			setToQuery(gpuQueries);
			catQuery = Query.GPU;
			break;
		case "Laptop":
			setToQuery(laptopQueries);
			catQuery = Query.LAPTOP;
			break;
		case "Mobile":
			setToQuery(mobileQueries);
			catQuery = Query.MOBILE;
			break;
		case "Motherboard":
			setToQuery(motherboardQueries);
			catQuery = Query.MOTHERBOARD;
			break;
		case "Multiple":
			setToQuery(multipleQueries);
			catQuery = Query.MULTIPLE;
			break;
		case "RAM":
			setToQuery(ramQueries);
			catQuery = Query.RAM;
			break;
		case "Tablet":
			setToQuery(tabletQueries);
			catQuery = Query.TABLET;
			break;
		default:
			break;
		}
	}

	@FXML
	private void queryAction() throws IOException {
		queryIndex = queryBox.getSelectionModel().getSelectedIndex();
	}

	@SuppressWarnings("restriction")
	@FXML
	private void runAction() throws IOException, SQWRLException {
		tableView.getItems().clear();
		tableView.getColumns().clear();
		int num = queryIndex + 1;
		switch (catQuery) {
		case CPU:
			SQWRLResult result = queryEngine.runSQWRLQuery("CPU Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Name");
				tableView.getColumns().addAll(nameColumn);
			} else if (queryIndex < 4) {
				TableColumn<Integer, Number> countColumn = createTableColumnNumber(result.getColumn(0), "Count");
				tableView.getColumns().addAll(countColumn);
			} else if (queryIndex == 4) {
				TableColumn<Integer, String> productColumn = createTableColumnString(result.getColumn(0),
						"Product Name");
				TableColumn<Integer, Number> speedColumn = createTableColumnFloat(result.getColumn(1), "Speed");
				tableView.getColumns().addAll(productColumn, speedColumn);
			} else if (queryIndex == 5) {
				TableColumn<Integer, Number> speedColumn = createTableColumnFloat(result.getColumn(0), "Speed");
				TableColumn<Integer, Number> countColumn = createTableColumnNumber(result.getColumn(1), "Count");
				tableView.getColumns().addAll(speedColumn, countColumn);
			} else if (queryIndex == 6) {
				TableColumn<Integer, String> brandColumn = createTableColumnString(result.getColumn(0), "Brand");
				TableColumn<Integer, String> seriesColumn = createTableColumnString(result.getColumn(1), "Series");
				TableColumn<Integer, Number> countColumn = createTableColumnNumber(result.getColumn(2), "Count");
				tableView.getColumns().addAll(brandColumn, seriesColumn, countColumn);
			} else if (queryIndex == 7) {
				TableColumn<Integer, String> productColumn = createTableColumnString(result.getColumn(0),
						"Product Name");
				TableColumn<Integer, String> brandColumn = createTableColumnString(result.getColumn(1), "Brand");
				TableColumn<Integer, String> seriesColumn = createTableColumnString(result.getColumn(2), "Series");
				TableColumn<Integer, Number> countColumn = createTableColumnInteger(result.getColumn(3), "Price");
				tableView.getColumns().addAll(productColumn, brandColumn, seriesColumn, countColumn);
			}

			break;
		case GPU:
			result = queryEngine.runSQWRLQuery("Graphic Card Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "GPU Brand");
				tableView.getColumns().addAll(nameColumn);
			} else if (queryIndex < 4) {
				TableColumn<Integer, Number> countColumn = createTableColumnNumber(result.getColumn(0), "Count");
				tableView.getColumns().addAll(countColumn);
			} else if (queryIndex == 4) {
				TableColumn<Integer, String> productColumn = createTableColumnString(result.getColumn(0),
						"Product Name");
				TableColumn<Integer, Number> speedColumn = createTableColumnInteger(result.getColumn(1), "Memory Size");
				tableView.getColumns().addAll(productColumn, speedColumn);
			} else if (queryIndex == 5) {
				TableColumn<Integer, String> productColumn = createTableColumnString(result.getColumn(0),
						"Product Name");
				TableColumn<Integer, Number> countColumn = createTableColumnInteger(result.getColumn(1), "Price");
				tableView.getColumns().addAll(productColumn, countColumn);
			}
			break;
		case LAPTOP:
			result = queryEngine.runSQWRLQuery("Laptop Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> priceColumn = createTableColumnInteger(result.getColumn(1), "Price");
				tableView.getColumns().addAll(priceColumn, nameColumn);
			} else if (queryIndex == 1) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> wvColumn = createTableColumnFloat(result.getColumn(1), "Weight Value");
				TableColumn<Integer, String> wuColumn = createTableColumnString(result.getColumn(2), "Weight Unit");
				tableView.getColumns().addAll(wvColumn, wuColumn, nameColumn);
			} else if (queryIndex == 2) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> svColumn = createTableColumnFloat(result.getColumn(1), "Size Value");
				TableColumn<Integer, String> suColumn = createTableColumnString(result.getColumn(2), "Size Unit");
				TableColumn<Integer, Number> priceColumn = createTableColumnInteger(result.getColumn(3), "Price");
				tableView.getColumns().addAll(svColumn, suColumn, nameColumn, priceColumn);
			} else if (queryIndex == 3) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> svColumn = createTableColumnFloat(result.getColumn(1), "Speed Value");
				TableColumn<Integer, String> suColumn = createTableColumnString(result.getColumn(2), "Speed Unit");
				TableColumn<Integer, Number> priceColumn = createTableColumnInteger(result.getColumn(3), "Price");
				tableView.getColumns().addAll(svColumn, suColumn, nameColumn, priceColumn);
			} else if (queryIndex == 4) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(1), "RAM Value");
				TableColumn<Integer, String> ruColumn = createTableColumnString(result.getColumn(2), "RAM Unit");
				TableColumn<Integer, Number> priceColumn = createTableColumnInteger(result.getColumn(3), "Price");
				tableView.getColumns().addAll(rvColumn, ruColumn, nameColumn, priceColumn);
			} else if (queryIndex == 5) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> svColumn = createTableColumnInteger(result.getColumn(1), "Storage Value");
				TableColumn<Integer, String> suColumn = createTableColumnString(result.getColumn(2), "Storage Unit");
				TableColumn<Integer, Number> priceColumn = createTableColumnInteger(result.getColumn(3), "Price");
				tableView.getColumns().addAll(svColumn, suColumn, nameColumn, priceColumn);
			} else if (queryIndex == 6) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> wvColumn = createTableColumnFloat(result.getColumn(1), "Weight Value");
				TableColumn<Integer, Number> suColumn = createTableColumnFloat(result.getColumn(2),
						"Screen Size Value");
				TableColumn<Integer, Number> priceColumn = createTableColumnInteger(result.getColumn(3), "Price");
				tableView.getColumns().addAll(wvColumn, suColumn, nameColumn, priceColumn);
			} else if (queryIndex == 7) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Company Name");
				TableColumn<Integer, Number> countColumn = createTableColumnNumber(result.getColumn(1), "Count");
				tableView.getColumns().addAll(nameColumn, countColumn);
			} else if (queryIndex == 8) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "CPU Series");
				TableColumn<Integer, Number> cvColumn = createTableColumnFloat(result.getColumn(1), "CPU Speed");
				TableColumn<Integer, Number> countColumn = createTableColumnNumber(result.getColumn(2), "Count");
				tableView.getColumns().addAll(cvColumn, countColumn, nameColumn);
			} else if (queryIndex == 9) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "CPU Series");
				TableColumn<Integer, Number> cvColumn = createTableColumnFloat(result.getColumn(1), "CPU Speed");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(2), "Min Price");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(3), "Max Price");
				TableColumn<Integer, Number> countColumn = createTableColumnNumber(result.getColumn(4), "Count");
				tableView.getColumns().addAll(nameColumn, cvColumn, minPColumn, maxPColumn, countColumn);
			}
			break;
		case MOBILE:
			result = queryEngine.runSQWRLQuery("Mobile Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "OS");
				TableColumn<Integer, Number> countColumn = createTableColumnInteger(result.getColumn(1), "Count");
				tableView.getColumns().addAll(nameColumn, countColumn);
			} else if (queryIndex == 1) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> hvColumn = createTableColumnInteger(result.getColumn(2),
						"Height Resolution");
				TableColumn<Integer, Number> wvColumn = createTableColumnInteger(result.getColumn(1),
						"Weight Resolution");
				tableView.getColumns().addAll(nameColumn, wvColumn, hvColumn);
			} else if (queryIndex == 2) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(1), "RAM Value");
				TableColumn<Integer, String> ruColumn = createTableColumnString(result.getColumn(2), "RAM Unit");
				TableColumn<Integer, Number> csColumn = createTableColumnFloat(result.getColumn(3), "CPU Speed Value");
				tableView.getColumns().addAll(nameColumn, rvColumn, ruColumn, csColumn);
			} else if (queryIndex == 3) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> wColumn = createTableColumnInteger(result.getColumn(1), "Weight");
				tableView.getColumns().addAll(nameColumn, wColumn);
			} else if (queryIndex == 4) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "CPU Chipset");
				TableColumn<Integer, Number> csColumn = createTableColumnFloat(result.getColumn(1), "CPU Speed");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(2), "Ram value");
				TableColumn<Integer, String> ruColumn = createTableColumnString(result.getColumn(3), "Ram unit");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(4), "Min Price");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(5), "Max Price");
				tableView.getColumns().addAll(nameColumn, csColumn, rvColumn, ruColumn, minPColumn, maxPColumn);
			} else if (queryIndex == 5) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Screen Size");
				TableColumn<Integer, Number> csColumn = createTableColumnInteger(result.getColumn(1),
						"Width Resolution");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(2),
						"Height Resolution");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(3), "Min Price");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(4), "Max Price");
				TableColumn<Integer, Number> ruColumn = createTableColumnNumber(result.getColumn(5), "Count");
				tableView.getColumns().addAll(nameColumn, csColumn, rvColumn, ruColumn, minPColumn, maxPColumn);
			} else if (queryIndex == 6) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				tableView.getColumns().addAll(nameColumn);
			}
			break;
		case MOTHERBOARD:
			result = queryEngine.runSQWRLQuery("Motherboard Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> pcColumn = createTableColumnInteger(result.getColumn(1), "Port Count");
				TableColumn<Integer, String> ptColumn = createTableColumnString(result.getColumn(2), "Port Type");
				TableColumn<Integer, Number> mmsColumn = createTableColumnInteger(result.getColumn(3),
						"Motherboard memory Slot");
				TableColumn<Integer, String> mmtColumn = createTableColumnString(result.getColumn(4),
						"Motherboard memory type");
				TableColumn<Integer, Number> mmmColumn = createTableColumnInteger(result.getColumn(5),
						"Motherboard max memory");
				tableView.getColumns().addAll(nameColumn, pcColumn, ptColumn, mmsColumn, mmtColumn, mmmColumn);
			} else if (queryIndex == 1) {
				TableColumn<Integer, Number> pcColumn = createTableColumnInteger(result.getColumn(0), "Port Count");
				TableColumn<Integer, String> ptColumn = createTableColumnString(result.getColumn(1), "Port Type");
				TableColumn<Integer, Number> mmsColumn = createTableColumnInteger(result.getColumn(2),
						"Motherboard memory Slot");
				TableColumn<Integer, String> mmtColumn = createTableColumnString(result.getColumn(3),
						"Motherboard memory type");
				TableColumn<Integer, Number> mmmColumn = createTableColumnInteger(result.getColumn(4),
						"Motherboard max memory");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(5), "max price");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(6), "min price");
				TableColumn<Integer, Number> cColumn = createTableColumnNumber(result.getColumn(7), "count");
				tableView.getColumns().addAll(pcColumn, ptColumn, mmsColumn, mmtColumn, mmmColumn, maxPColumn,
						minPColumn, cColumn);
			}
			break;
		case MULTIPLE:
			result = queryEngine.runSQWRLQuery("Multiple Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Company Name");
				tableView.getColumns().addAll(nameColumn);
			} else if (queryIndex == 1) {
				TableColumn<Integer, String> nmColumn = createTableColumnString(result.getColumn(0), "Mobile Name");
				TableColumn<Integer, Number> pmColumn = createTableColumnInteger(result.getColumn(1), "Price Mobile");
				TableColumn<Integer, String> ntColumn = createTableColumnString(result.getColumn(2), "Tablet Name");
				TableColumn<Integer, Number> ptColumn = createTableColumnInteger(result.getColumn(3), "Price Tablet");
				tableView.getColumns().addAll(nmColumn, pmColumn, ntColumn, ptColumn);
			} else if (queryIndex == 2) {
				TableColumn<Integer, String> nmColumn = createTableColumnString(result.getColumn(0), "Mobile Name");
				TableColumn<Integer, String> tmColumn = createTableColumnString(result.getColumn(1), "Tablet Name");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(2), "RAM Value");
				TableColumn<Integer, Number> cvColumn = createTableColumnFloat(result.getColumn(3), "CPU Value");
				TableColumn<Integer, Number> dpColumn = createTableColumnInteger(result.getColumn(4),
						"Difference Price");
				tableView.getColumns().addAll(nmColumn, tmColumn, rvColumn, cvColumn, dpColumn);
			} else if (queryIndex == 3) {
				TableColumn<Integer, Number> nameColumn = createTableColumnInteger(result.getColumn(0), "Range Value");
				TableColumn<Integer, Number> cColumn = createTableColumnNumber(result.getColumn(1), "Count");
				tableView.getColumns().addAll(nameColumn, cColumn);
			} else if (queryIndex == 4) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "name");
				TableColumn<Integer, Number> cColumn = createTableColumnFloat(result.getColumn(1), "count");
				tableView.getColumns().addAll(nameColumn, cColumn);
			} else if (queryIndex == 5) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Name");
				TableColumn<Integer, Number> maxColumn = createTableColumnInteger(result.getColumn(1), "Max Price");
				TableColumn<Integer, Number> cColumn = createTableColumnNumber(result.getColumn(2), "Count");
				tableView.getColumns().addAll(nameColumn, maxColumn, cColumn);
			} else if (queryIndex == 6) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Name");
				TableColumn<Integer, Number> minColumn = createTableColumnInteger(result.getColumn(1), "Min Price");
				TableColumn<Integer, Number> cColumn = createTableColumnNumber(result.getColumn(2), "Count");
				tableView.getColumns().addAll(nameColumn, minColumn, cColumn);
			} else if (queryIndex == 7) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Name");
				tableView.getColumns().addAll(nameColumn);
			} else if (queryIndex == 8) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Mobile Name");
				TableColumn<Integer, Number> csColumn = createTableColumnNumber(result.getColumn(1), "size n");
				TableColumn<Integer, Number> rvColumn = createTableColumnNumber(result.getColumn(2), "size m");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(3), "Price laptop");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(4), "Price mobile");
				TableColumn<Integer, Number> ruColumn = createTableColumnInteger(result.getColumn(5), "max price");
				TableColumn<Integer, Number> suColumn = createTableColumnInteger(result.getColumn(6), "subtract");
				tableView.getColumns().addAll(nameColumn, csColumn, rvColumn, ruColumn, minPColumn, maxPColumn,
						suColumn);
			} else if (queryIndex == 9) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Mobile Name");
				TableColumn<Integer, String> yColumn = createTableColumnString(result.getColumn(1), "y Name");
				tableView.getColumns().addAll(nameColumn, yColumn);
			}
			break;
		case RAM:
			result = queryEngine.runSQWRLQuery("RAM Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> pcColumn = createTableColumnInteger(result.getColumn(1), "Capacity");
				TableColumn<Integer, String> ptColumn = createTableColumnString(result.getColumn(2), "Unit");
				TableColumn<Integer, Number> mmmColumn = createTableColumnInteger(result.getColumn(3), "Price");
				tableView.getColumns().addAll(nameColumn, pcColumn, ptColumn, mmmColumn);
			} else if (queryIndex == 1) {
				TableColumn<Integer, Number> pcColumn = createTableColumnInteger(result.getColumn(0), "Capacity");
				TableColumn<Integer, String> ptColumn = createTableColumnString(result.getColumn(1), "Unit");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(2), "max price");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(3), "min price");
				TableColumn<Integer, Number> cColumn = createTableColumnNumber(result.getColumn(4), "count");
				tableView.getColumns().addAll(pcColumn, ptColumn, maxPColumn, minPColumn, cColumn);
			}
			break;
		case TABLET:
			result = queryEngine.runSQWRLQuery("Mobile Query " + (num < 10 ? "0" : "") + num);
			for (int i = 0; i < result.getNumberOfRows(); i++) {
				tableView.getItems().add(i);
			}

			if (queryIndex == 0) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "OS");
				TableColumn<Integer, Number> countColumn = createTableColumnInteger(result.getColumn(1), "Count");
				tableView.getColumns().addAll(nameColumn, countColumn);
			} else if (queryIndex == 1) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> hvColumn = createTableColumnInteger(result.getColumn(2),
						"Height Resolution");
				TableColumn<Integer, Number> wvColumn = createTableColumnInteger(result.getColumn(1),
						"Weight Resolution");
				tableView.getColumns().addAll(nameColumn, wvColumn, hvColumn);
			} else if (queryIndex == 2) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(1), "RAM Value");
				TableColumn<Integer, String> ruColumn = createTableColumnString(result.getColumn(2), "RAM Unit");
				TableColumn<Integer, Number> csColumn = createTableColumnFloat(result.getColumn(3), "CPU Speed Value");
				tableView.getColumns().addAll(nameColumn, rvColumn, ruColumn, csColumn);
			} else if (queryIndex == 3) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Product Name");
				TableColumn<Integer, Number> wColumn = createTableColumnInteger(result.getColumn(1), "Weight");
				tableView.getColumns().addAll(nameColumn, wColumn);
			} else if (queryIndex == 4) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "CPU Chipset");
				TableColumn<Integer, Number> csColumn = createTableColumnFloat(result.getColumn(1), "CPU Speed");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(2), "Ram value");
				TableColumn<Integer, String> ruColumn = createTableColumnString(result.getColumn(3), "Ram unit");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(4), "Min Price");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(5), "Max Price");
				tableView.getColumns().addAll(nameColumn, csColumn, rvColumn, ruColumn, minPColumn, maxPColumn);
			} else if (queryIndex == 5) {
				TableColumn<Integer, String> nameColumn = createTableColumnString(result.getColumn(0), "Screen Size");
				TableColumn<Integer, Number> csColumn = createTableColumnInteger(result.getColumn(1),
						"Width Resolution");
				TableColumn<Integer, Number> rvColumn = createTableColumnInteger(result.getColumn(2),
						"Height Resolution");
				TableColumn<Integer, Number> minPColumn = createTableColumnInteger(result.getColumn(3), "Min Price");
				TableColumn<Integer, Number> maxPColumn = createTableColumnInteger(result.getColumn(4), "Max Price");
				TableColumn<Integer, Number> ruColumn = createTableColumnNumber(result.getColumn(5), "Count");
				tableView.getColumns().addAll(nameColumn, csColumn, rvColumn, ruColumn, minPColumn, maxPColumn);
			}
			break;
		default:
			break;
		}
	}

	private void setToQuery(ObservableList<String> queries) {
		queryBox.getItems().addAll(queries);
	}

	private TableColumn<Integer, String> createTableColumnString(List<SQWRLResultValue> list, String columnName) {
		TableColumn<Integer, String> column = new TableColumn<>(columnName);
		column.setCellValueFactory(cellData -> {
			Integer rowIndex = cellData.getValue();
			String s = list.get(rowIndex).toString().replaceAll("^\"|\"$", "");
			return new ReadOnlyStringWrapper(s);

		});
		return column;
	}

	private TableColumn<Integer, Number> createTableColumnNumber(List<SQWRLResultValue> list, String columnName) {
		TableColumn<Integer, Number> column = new TableColumn<>(columnName);
		column.setCellValueFactory(cellData -> {
			Integer rowIndex = cellData.getValue();
			Integer s = null;
			try {
				s = list.get(rowIndex).asLiteralResult().getInt();
			} catch (SQWRLException e) {
				e.printStackTrace();
			}
			return new ReadOnlyIntegerWrapper(s);
		});
		return column;
	}

	private TableColumn<Integer, Number> createTableColumnInteger(List<SQWRLResultValue> list, String columnName) {
		TableColumn<Integer, Number> column = new TableColumn<>(columnName);
		column.setCellValueFactory(cellData -> {
			Integer rowIndex = cellData.getValue();
			Long s = null;
			try {
				s = list.get(rowIndex).asLiteralResult().getInteger().longValue();
			} catch (SQWRLException e) {
				e.printStackTrace();
			}
			return new ReadOnlyLongWrapper(s);
		});
		return column;
	}

	private TableColumn<Integer, Number> createTableColumnFloat(List<SQWRLResultValue> list, String columnName) {
		TableColumn<Integer, Number> column = new TableColumn<>(columnName);
		column.setCellValueFactory(cellData -> {
			Integer rowIndex = cellData.getValue();
			Float s = null;
			try {
				s = list.get(rowIndex).asLiteralResult().getFloat();
			} catch (SQWRLException e) {
				e.printStackTrace();
			}
			return new ReadOnlyFloatWrapper(s);
		});
		return column;
	}
}