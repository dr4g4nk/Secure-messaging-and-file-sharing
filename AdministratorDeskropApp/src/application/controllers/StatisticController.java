package application.controllers;

import java.util.ArrayList;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.model.Row;
import org.unibl.etf.model.Statistic;
import org.unibl.etf.model.util.Util;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StatisticController {

	@FXML
	private TableView<Row> statisticTable;

	@FXML
	private TableColumn<Row, Long> rbColumn;

	@FXML
	private TableColumn<Row, String> loginTimeColumn;

	@FXML
	private TableColumn<Row, String> logoutTimeColumn;

	@FXML
	private TableColumn<Row, Long> durationColumn;

	private WebTarget target;
	private Gson gson;
	private static String username;

	@FXML
	private void initialize() {
		gson = new Gson();
		target = Util.getTarget();
		Invocation.Builder inv = target.path("statistic").path(username).request(MediaType.APPLICATION_JSON);
		Response response = inv.get();

		if (response.getStatus() == 200) {
			Statistic statistic = new Statistic(gson.fromJson(response.readEntity(String.class), Statistic.class));
			ArrayList<Row> rows = statistic.getRows();

			new Thread() {
				public void run() {
					rbColumn.setCellValueFactory(new PropertyValueFactory<Row, Long>("ID"));
					durationColumn.setCellValueFactory(new PropertyValueFactory<Row, Long>("duration"));
					loginTimeColumn.setCellValueFactory(new PropertyValueFactory<Row, String>("loginTime"));
					logoutTimeColumn.setCellValueFactory(new PropertyValueFactory<Row, String>("logoutTime"));

					statisticTable.getItems().addAll(rows);
				};
			}.start();
		}
	}

	public static void setUsername(String str) {
		username = str;
	}
}
