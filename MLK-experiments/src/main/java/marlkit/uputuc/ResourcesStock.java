package marlkit.uputuc;

import java.util.EnumMap;
import java.util.Map;

public class ResourcesStock {
	private EnumMap<Resource, ResourceSlot> resources;

	public ResourcesStock() {
	    this.resources = new EnumMap<>(Resource.class);
	}

	public ResourcesStock(EnumMap<Resource, ResourceSlot> resources) {
		super();
		this.resources = resources;
	}

	public Map<Resource, ResourceSlot> getResources() {
		return resources;
	}

	public void setResources(EnumMap<Resource, ResourceSlot> resources) {
		this.resources = resources;
	}


	public int getQuantity(Resource resource) {
		ResourceSlot rs = resources.getOrDefault(resource, null);
		if (rs != null) {
			return rs.getValue();			
		}
		return 0;
	}
	
	public int[] getVectorizedStock() {
		int[] vectorizedStock = new int[Resource.values().length];
		for (Resource res : resources.keySet()) {
			vectorizedStock[res.ordinal()] = getQuantity(res);
		}
		
		return vectorizedStock;
	}
	
	public void add(Resource resource, int quantity) {
        if (resources.containsKey(resource)) {
            resources.get(resource).add(quantity);
        } else {
            resources.put(resource, new ResourceSlot(resource, quantity));
        }
    }
	
	public void reset() {
        resources.clear(); // vide complètement la map
    }
	
	public boolean containsKey(Resource resource) {
        return resources.containsKey(resource);
    }
	
}
