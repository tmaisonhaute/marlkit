package util.grafana;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agent.MLKAgent;
import learning.Experience;
import util.Pair;

public class LearningData {
    private int stepCount;
    private Pair<Map<MLKAgent, Pair<Double, Integer>>, Map<String, Pair<String, Double>>> currentEp;
    private final List<Pair<Map<MLKAgent, Double>, Map<String, Double>>> averageEpisodesReward;
    private final Set<String> agents;
    private final Set<String> extraKeys;
    private String logFilePath;
    private int totalEpisodes;


    public LearningData() {
        this.currentEp = new Pair<>(new HashMap<>(), new HashMap<>());
        this.averageEpisodesReward = new ArrayList<>();
        this.agents = new HashSet<>();
        this.extraKeys = new HashSet<>();
        this.totalEpisodes = 1;
    }

    /**
     * Returns the path to the log file.
     *
     * @return the path to the log file
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * Adds a step to the learning data, registering experiences and extras.
     * If the step count reaches 100, it finalizes the episode.
     *
     * @param step the step data containing experiences and optional extras
     */
    public void addStep(StepData step) {
        registerExperiences(step.getExperiences());
        if (step.getExtra().isPresent()) {
            registerExtras(step.getExtra().get());
        }
        stepCount++;
        if (stepCount == 100) {
            finalizeEpisode();
        }
    }

    /**
     * Adds a step to the learning data, registering experiences and extras.
     * If the step count reaches 100, it finalizes the episode.
     *
     * @param experiences a map of agents to their experiences
     */
    private void registerExperiences(Map<MLKAgent, Experience> experiences) {
        Map<MLKAgent, Pair<Double, Integer>> rewardsSum = currentEp.getFirst();
        for (Map.Entry<MLKAgent, Experience> entry : experiences.entrySet()) {
            MLKAgent agent = entry.getKey();
            Experience experience = entry.getValue();
            agents.add(agent.toString());
            double reward = experience.getReward().getValue();
            rewardsSum.putIfAbsent(agent, new Pair<>(0.0, 0));
            rewardsSum.computeIfPresent(agent, (_, currentSum) -> new Pair<>(currentSum.getFirst() + reward, currentSum.getSecond() + 1));
        }
    }

    /**
     * Registers extras in the current episode.
     * It updates the sum of each extra value and keeps track of unique extra keys.
     *
     * @param extras a list of extras to register
     */
    private void registerExtras(List<Extra> extras) {
        Map<String, Pair<String, Double>> extraSum = currentEp.getSecond();
        for (Extra extra : extras) {
            String key = extra.toString();
            double value = extra.toDouble();
            extraKeys.add(key);
            extraSum.putIfAbsent(key, new Pair<>(key, 0.0));
            extraSum.computeIfPresent(key, (_, currentSum) -> new Pair<>(currentSum.getFirst(), currentSum.getSecond() + value));
        }
    }

    /**
     * Finalizes the current episode by calculating average rewards and extras,
     * and resetting the step count and current episode data.
     */
    private void finalizeEpisode() {
        Map<MLKAgent, Double> averageRewards = getAverageRewards(currentEp.getFirst());
        Map<String, Double> averageExtras = getAverageExtras(currentEp.getSecond());
        averageEpisodesReward.add(new Pair<>(averageRewards, averageExtras));
        stepCount = 0;
        currentEp = new Pair<>(new HashMap<>(), new HashMap<>());
    }

    /**
     * Calculates the average rewards for each agent based on the sum of rewards and counts.
     *
     * @param rewardsSum a map of agents to their total reward and count
     * @return a map of agents to their average reward
     */
    private Map<MLKAgent, Double> getAverageRewards(Map<MLKAgent, Pair<Double, Integer>> rewardsSum) {
        Map<MLKAgent, Double> averageRewards = new HashMap<>();
        for (Map.Entry<MLKAgent, Pair<Double, Integer>> entry : rewardsSum.entrySet()) {
            MLKAgent agent = entry.getKey();
            Pair<Double, Integer> sumAndCount = entry.getValue();
            double averageReward = sumAndCount.getFirst() / sumAndCount.getSecond();
            averageRewards.put(agent, averageReward);
        }
        return averageRewards;
    }

    /**
     * Calculates the average value of each extra based on the sum of values.
     *
     * @param extrasSum a map of extra keys to their total value
     * @return a map of extra keys to their average value
     */
    private Map<String, Double> getAverageExtras(Map<String, Pair<String, Double>> extrasSum) {
        Map<String, Double> averageExtras = new HashMap<>();
        for (Map.Entry<String, Pair<String, Double>> entry : extrasSum.entrySet()) {
            String key = entry.getKey();
            double averageValue = entry.getValue().getSecond();
            averageExtras.put(key, averageValue);
        }
        return averageExtras;
    }

    /**
     * Initializes the log file with the specified rows.
     * This method should be called before using {@link #writeLog(String)} to write to the log.
     * It creates a new log file in the "logs" directory with a timestamp in the filename.
     *
     * @param rows the initial lines to write to the log file
     * @throws IOException if a write error occurs
     */
    public void initLogfile(List<String> rows) throws IOException {
        Path logDir = Path.of("logs");
        if (!Files.exists(logDir)) {
            Files.createDirectories(logDir);
        }

        logFilePath = "logs/log_" + Instant.now().getEpochSecond() + ".csv";
        BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFilePath, true));

        logWriter.write("Episode" + "," + String.join(",", rows) + "\n");

        logWriter.flush();
        logWriter.close();
    }

    /**
     * Writes a log message into the log file.
     * It appends the episode number and the message to the log file.
     * The episode number is incremented for each call to this method.
     *
     * @param message the message to write to the log file
     * @throws IOException if a write error occurs
     */
    public void writeLog(String message) throws IOException {
        BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFilePath, true));
        StringBuilder formattedMessage = new StringBuilder();
        for (String line : message.split("\n")) {
            if (line.isEmpty()) {
                continue;
            }
            formattedMessage.append(totalEpisodes).append(",").append(line).append("\n");
            totalEpisodes++;
        }
        logWriter.write(formattedMessage.toString());
        logWriter.close();
    }

    /**
     * Generates a graph URL for the learning data.
     * It creates a Grafana instance, retrieves or creates a datasource,
     * creates a dashboard, and optionally exports it to a PNG file.
     *
     * @param graphName the name of the graph to generate
     * @param exportToFile whether to export the graph to a PNG file
     * @return the URL of the generated graph
     * @throws IOException if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    public String generateGraph(String graphName, boolean exportToFile) throws IOException, URISyntaxException {
        Grafana grafana = null;
        if (exportToFile) {
            grafana = new Grafana(logFilePath, graphName + ".png");
        } else {
            grafana = new Grafana(logFilePath);
        }
        String ds_uid = grafana.getOrCreateDatasource(graphName);
        Pair<String, String> dashboard = grafana.createDashboard(ds_uid);
        String dash_uid = dashboard.getFirst();
        String slug = dashboard.getSecond();
        if (exportToFile) {
            grafana.renderPNG(dash_uid, slug);
        }
        return grafana.getUrl() + "/d/" + dash_uid + "/" + slug;
    }

    public int getStepCount() {
        return stepCount;
    }

    /**
     * Returns the number of average episodes recorded.
     *
     * @return the count of average episodes
     */
    public int getAverageEpisodesCount() {
        return averageEpisodesReward.size();
    }

    /**
     * Returns the list of average episodes rewards.
     * Each entry in the list is a pair containing a map of agents and their average rewards,
     * and a map of extra keys and their average values.
     *
     * @return the list of average episodes rewards
     */
    public  List<Pair<Map<MLKAgent, Double>, Map<String, Double>>> getAverageEpisodesReward() {
        return averageEpisodesReward;
    }

    /**
     * Clears the current episodes data, resetting the average episodes rewards.
     */
    public void clearEpisodes() {
        averageEpisodesReward.clear();
    }

    /**
     * Clears the current step data, resetting the step count and current episode data.
     */
    public void clearSteps() {
        stepCount = 0;
        currentEp = new Pair<>(new HashMap<>(), new HashMap<>());
    }

    public Set<String> getAgents() {
        return agents;
    }

    public Set<String> getExtraKeys() {
        return extraKeys;
    }
}