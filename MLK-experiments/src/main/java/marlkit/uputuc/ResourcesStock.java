package marlkit.uputuc;

import java.util.EnumMap;
import java.util.Map;

public class ResourcesStock {
	private EnumMap<Resource, ResourceSlot> resources;

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
	
	
}
