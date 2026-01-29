package marlkit.uputuc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.policy.PolicyInput;
import util.Pair;

public class WrapperObservationBuyVector implements WrapperPolicyInputVector {

	@Override
    public double[] transform(PolicyInput observation) {
        ObservationBuy obs = (ObservationBuy) observation;
        List<Pair<ResourcesStock, Double>> stocksAndDistances = obs.getStocksAndDistances();

        int sizePerStock = Resource.values().length + 1;
        double[] vector = new double[stocksAndDistances.size() * sizePerStock];

        convertStocksToVector(stocksAndDistances, vector, sizePerStock);

        return vector;
    }

    @Override
    public Observation transform(double[] vector) {
        ObservationBuy observation = new ObservationBuy();
        List<Pair<ResourcesStock, Double>> stocksAndDistances = new ArrayList<>();

        int sizePerStock = Resource.values().length + 1;

        convertVectorToStocks(vector, stocksAndDistances, sizePerStock);

        observation.setStocksAndDistances(stocksAndDistances);
        return observation;
    }

    protected void convertStocksToVector(List<Pair<ResourcesStock, Double>> stocksAndDistances, 
                                           double[] vector, int sizePerStock) {
        for (int i = 0; i < stocksAndDistances.size(); i++) {
            Pair<ResourcesStock, Double> pair = stocksAndDistances.get(i);
            ResourcesStock stock = pair.getFirst();
            Double distance = pair.getSecond();

            int baseIndex = i * sizePerStock;

            convertResourceQuantitiesToVector(stock, vector, baseIndex);
            vector[baseIndex + Resource.values().length] = distance;
        }
    }

    protected void convertResourceQuantitiesToVector(ResourcesStock stock, double[] vector, int baseIndex) {
        for (Resource resource : Resource.values()) {
            int resourceIndex = resource.ordinal();
            vector[baseIndex + resourceIndex] = stock.getQuantity(resource);
        }
    }

    protected void convertVectorToStocks(double[] vector, 
                                           List<Pair<ResourcesStock, Double>> stocksAndDistances, 
                                           int sizePerStock) {
        for (int baseIndex = 0; baseIndex < vector.length; baseIndex += sizePerStock) {
            double distance = vector[baseIndex + Resource.values().length];

            EnumMap<Resource, ResourceSlot> resourceMap = convertVectorToResourceMap(vector, baseIndex);

            if (!resourceMap.isEmpty()) {
                ResourcesStock stock = new ResourcesStock(resourceMap);
                stocksAndDistances.add(new Pair<>(stock, distance));
            }
        }
    }

    protected EnumMap<Resource, ResourceSlot> convertVectorToResourceMap(double[] vector, int baseIndex) {
        EnumMap<Resource, ResourceSlot> resourceMap = new EnumMap<>(Resource.class);

        for (int j = 0; j < Resource.values().length; j++) {
            double quantity = vector[baseIndex + j];
            if (quantity > 0) {
                Resource resource = Resource.values()[j];
                ResourceSlot slot = new ResourceSlot(resource, (int)quantity);
                resourceMap.put(resource, slot);
            }
        }

        return resourceMap;
    }
}
