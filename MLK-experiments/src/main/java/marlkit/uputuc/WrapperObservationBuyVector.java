package marlkit.uputuc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import util.Pair;

public class WrapperObservationBuyVector implements WrapperObservationVector {

	@Override
    public double[] transform(Observation observation) {
        ObservationBuy obs = (ObservationBuy) observation;
        List<Pair<ResourcesStock, Double>> stocksAndDistances = obs.getStocksAndDistances();

        int sizePerStock = Resource.values().length + 1;
        double[] vector = new double[stocksAndDistances.size() * sizePerStock];

        for (int i = 0; i < stocksAndDistances.size(); i++) {
            Pair<ResourcesStock, Double> pair = stocksAndDistances.get(i);
            ResourcesStock stock = pair.getFirst();
            Double distance = pair.getSecond();

            int baseIndex = i * sizePerStock;

            for (Resource resource : Resource.values()) {
                int resourceIndex = resource.ordinal();
                vector[baseIndex + resourceIndex] = stock.getQuantity(resource);
            }

            vector[baseIndex + Resource.values().length] = distance;
        }

        return vector;
    }

    @Override
    public Observation transform(double[] vector) {
        ObservationBuy observation = new ObservationBuy();
        List<Pair<ResourcesStock, Double>> stocksAndDistances = new ArrayList<>();

        int sizePerStock = Resource.values().length + 1;

        for (int baseIndex = 0; baseIndex < vector.length; baseIndex+=sizePerStock) {
            double distance = vector[baseIndex + Resource.values().length];

            // Create a ResourcesStock for this entry
            EnumMap<Resource, ResourceSlot> resourceMap = new EnumMap<>(Resource.class);

            // Add resources with non-zero values
            for (int j = 0; j < Resource.values().length; j++) {
                double quantity = vector[baseIndex + j];
                if (quantity > 0) {
                    Resource resource = Resource.values()[j];
                    ResourceSlot slot = new ResourceSlot(resource, (int)quantity);
                    resourceMap.put(resource, slot);
                }
            }

            // Only add if there are resources
            if (!resourceMap.isEmpty()) {
                ResourcesStock stock = new ResourcesStock(resourceMap);
                stocksAndDistances.add(new Pair<>(stock, distance));
            }
        }

        observation.setStocksAndDistances(stocksAndDistances);
        return observation;
    }
}
