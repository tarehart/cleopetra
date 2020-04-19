package dk.aau.cs.ds306e18.tournament.ui;

import dk.aau.cs.ds306e18.tournament.model.match.MatchResultDependencyException;
import dk.aau.cs.ds306e18.tournament.model.match.Series;
import dk.aau.cs.ds306e18.tournament.utility.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditSeriesScoreController extends DraggablePopupWindow {

    private static final Paint BLUE_FILL = Paint.valueOf("#6a82fc");
    private static final Paint ORANGE_FILL = Paint.valueOf("#f5af18");

    @FXML private CheckBox matchOverCheckBox;
    @FXML private Label teamOneNameLabel;
    @FXML private Label teamTwoNameLabel;
    @FXML private HBox scoresContainer;
    @FXML private Button extendSeriesButton;
    @FXML private Button shortenSeriesButton;
    @FXML private Button saveButton;

    private Series series;
    private List<EditMatchScoreController> scoreControllers = new ArrayList<>();

    /**
     * Checks the scores in the spinners. If they are okay, save button is enabled, other match-is-over checkbox
     * and save button is disabled accordingly.
     */
    private void checkScoresAndUpdateUI() {

        // We assume everything is okay until something proves otherwise
        boolean saveButtonDisable = false;
        boolean matchOverDisable = false;
        boolean matchOver = true;

        for (EditMatchScoreController matchScore : scoreControllers) {

            Optional<Integer> teamOneScore = matchScore.getTeamOneScore();
            Optional<Integer> teamTwoScore = matchScore.getTeamTwoScore();

            if (!teamOneScore.isPresent() || !teamTwoScore.isPresent()) {
                // We have an unknown score, so we do not allow saving
                saveButtonDisable = true;

            } else if (teamOneScore.equals(teamTwoScore)) {
                // They are tied in a match, so the series can not be over
                matchOverDisable = true;
                matchOver = false;
            }
        }

        saveButton.setDisable(saveButtonDisable);
        matchOverCheckBox.setDisable(matchOverDisable);
        matchOverCheckBox.setSelected(matchOver);
    }

    public void setSeries(Series series) {
        if (series == null) {
            closeWindow();
        }
        this.series = series;

        teamOneNameLabel.setText(series.getTeamOne().getTeamName());
        teamTwoNameLabel.setText(series.getTeamTwo().getTeamName());
        if (series.isTeamOneBlue()) {
            teamOneNameLabel.setTextFill(BLUE_FILL);
            teamTwoNameLabel.setTextFill(ORANGE_FILL);
        } else {
            teamOneNameLabel.setTextFill(ORANGE_FILL);
            teamTwoNameLabel.setTextFill(BLUE_FILL);
        }

        setupScores();

        matchOverCheckBox.setSelected(series.hasBeenPlayed());
    }

    /**
     * Setup score editors for each match in the series based on the model's current state.
     */
    private void setupScores() {
        scoresContainer.getChildren().clear();
        scoreControllers.clear();

        for (int i = 0; i < series.getSeriesLength(); i++) {
            EditMatchScoreController scoreController = EditMatchScoreController.loadNew(this::checkScoresAndUpdateUI);
            scoresContainer.getChildren().add(scoreController.getRoot());
            scoreControllers.add(scoreController);
            scoreController.setScores(
                    series.getTeamOneScore(i),
                    series.getTeamTwoScore(i)
            );
        }
    }

    @FXML
    public void windowDragged(MouseEvent mouseEvent) {
        super.windowDragged(mouseEvent);
    }

    @FXML
    public void windowPressed(MouseEvent mouseEvent) {
        super.windowPressed(mouseEvent);
    }

    @FXML
    private void onCancelBtnPressed(ActionEvent actionEvent) {
        closeWindow();
    }

    private void closeWindow() {
        Stage window = (Stage) teamOneNameLabel.getScene().getWindow();
        window.close();
    }

    @FXML
    private void onSaveBtnPressed(ActionEvent actionEvent) {

        List<Integer> teamOneScores = new ArrayList<>();
        List<Integer> teamTwoScores = new ArrayList<>();
        for (EditMatchScoreController scoreController : scoreControllers) {
            teamOneScores.add(scoreController.getTeamOneScore().get());
            teamTwoScores.add(scoreController.getTeamTwoScore().get());
        }

        boolean played = matchOverCheckBox.isSelected();

        boolean force = false;
        while (true) {
            try {

                series.setScores(
                        scoreControllers.size(),
                        teamOneScores,
                        teamTwoScores,
                        played,
                        force);
                closeWindow();
                break;

            } catch (MatchResultDependencyException e) {
                // An MatchResultDependencyException is thrown if the outcome has changed and subsequent matches depends on this outcome
                // Ask if the user wants to proceed
                force = Alerts.confirmAlert("The outcome of this match has changed", "This change will reset the subsequent matches. Do you want to proceed?");
            }
        }
    }

    public void onActionExtendSeriesButton(ActionEvent actionEvent) {
        for (int i = 0; i < 2; i++) {
            EditMatchScoreController scoreController = EditMatchScoreController.loadNew(this::checkScoresAndUpdateUI);
            scoresContainer.getChildren().add(scoreController.getRoot());
            scoreControllers.add(scoreController);
            scoreController.setScores(0, 0);
        }
        saveButton.getScene().getWindow().sizeToScene();
        checkScoresAndUpdateUI();
    }

    public void onActionShortenSeriesButton(ActionEvent actionEvent) {
        int oldLength = scoreControllers.size();
        if (oldLength > 1) {
            scoresContainer.getChildren().remove(oldLength - 2, oldLength);
            scoreControllers.remove(oldLength - 1);
            scoreControllers.remove(oldLength - 2);
        }
        saveButton.getScene().getWindow().sizeToScene();
        checkScoresAndUpdateUI();
    }
}
