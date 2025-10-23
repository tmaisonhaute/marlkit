package marlkit.uputuc.unite;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import agent.MLKAgent;
import marlkit.uputuc.Resource;
import marlkit.uputuc.ResourceSlot;
import marlkit.uputuc.TradeProposal;

public class UniteProductionTest {
    
    private UniteProduction uniteProduction;
    private MLKAgent mockAgent;
    
    @BeforeMethod
    public void setUp() {
    	MockitoAnnotations.openMocks(this);

        mockAgent = Mockito.mock(MLKAgent.class);

        uniteProduction = new UniteProduction(null, 0, 0);

        uniteProduction.stock.clear();
        uniteProduction.stock.put(Resource.AZENE, new ResourceSlot(Resource.AZENE, 10));
        uniteProduction.stock.put(Resource.BOGD, new ResourceSlot(Resource.BOGD, 15));
        uniteProduction.stock.put(Resource.CARBOL, new ResourceSlot(Resource.CARBOL, 0));

    }
    
    @Test
    public void givenTradeProposalsWithDifferentDistances_whenOrderRequests_thenSortedByDistance() {
        // Given
        List<TradeProposal> tradeRequests = new ArrayList<>();
        tradeRequests.add(new TradeProposal(Resource.AZENE, -5, 1.0f, 10.0, mockAgent, mockAgent));
        tradeRequests.add(new TradeProposal(Resource.BOGD, -3, 1.0f, 5.0, mockAgent, mockAgent));
        tradeRequests.add(new TradeProposal(Resource.CARBOL, -2, 1.0f, 15.0, mockAgent, mockAgent));
        
        // When
        uniteProduction.orderRequests(tradeRequests);
        
        // Then
        assertThat(tradeRequests.size()).isEqualTo(3);
        assertThat(tradeRequests.get(0).getDistance()).isEqualTo(5.0);
        assertThat(tradeRequests.get(1).getDistance()).isEqualTo(10.0);
        assertThat(tradeRequests.get(2).getDistance()).isEqualTo(15.0);
    }
    
    @Test
    public void givenTradeProposalsWithAvailableStock_whenSatisfyRequests_thenStockReducedAndAllRequestsSatisfied() {
        // Given
        List<TradeProposal> tradeRequests = new ArrayList<>();
        tradeRequests.add(new TradeProposal(Resource.AZENE, -5, 1.0f, 10.0, mockAgent, mockAgent));
        tradeRequests.add(new TradeProposal(Resource.BOGD, -3, 1.0f, 5.0, mockAgent, mockAgent));
        
        // When
        uniteProduction.satisfyRequests(tradeRequests);
        
        // Then
        assertThat(uniteProduction.stock.get(Resource.AZENE).getValue()).isEqualTo(5); // 10 - 5
        assertThat(uniteProduction.stock.get(Resource.BOGD).getValue()).isEqualTo(12); // 15 - 3
        assertThat(tradeRequests.size()).isEqualTo(2); // No requests removed
    }
    
    @Test
    public void givenTradeProposalsWithPartiallyAvailableStock_whenSatisfyRequests_thenUnavailableRequestsRemoved() {
        // Given
        List<TradeProposal> tradeRequests = new ArrayList<>();
        tradeRequests.add(new TradeProposal(Resource.AZENE, -15, 1.0f, 10.0, mockAgent, mockAgent)); // More than stock
        tradeRequests.add(new TradeProposal(Resource.BOGD, -3, 1.0f, 5.0, mockAgent, mockAgent));
        tradeRequests.add(new TradeProposal(Resource.CARBOL, -8, 1.0f, 15.0, mockAgent, mockAgent)); // More than stock
        
        // When
        uniteProduction.satisfyRequests(tradeRequests);
        
        // Then
        assertThat(uniteProduction.stock.get(Resource.AZENE).getValue()).isEqualTo(0); // All used
        assertThat(uniteProduction.stock.get(Resource.BOGD).getValue()).isEqualTo(12); // 15 - 3
        assertThat(uniteProduction.stock.get(Resource.CARBOL).getValue()).isEqualTo(0); // Impossible
        assertThat(tradeRequests.size()).isEqualTo(2); // One requests removed
        assertThat(tradeRequests.get(0).getType()).isEqualTo(Resource.AZENE); 
        assertThat(tradeRequests.get(1).getType()).isEqualTo(Resource.BOGD); 
    }
    
    @Test
    public void givenTradeProposalsWithUnavailableResource_whenSatisfyRequests_thenRequestRemoved() {
        // Given
        List<TradeProposal> tradeRequests = new ArrayList<>();
        tradeRequests.add(new TradeProposal(Resource.AZENE, -5, 1.0f, 10.0, mockAgent, mockAgent));
        
        // Remove AZENE from stock
        uniteProduction.stock.remove(Resource.AZENE);
        
        // When
        uniteProduction.satisfyRequests(tradeRequests);
        
        // Then
        assertThat(tradeRequests).isEmpty(); // Request for unavailable resource removed
    }
    
    @Test
    public void givenTradeProposalsWithDifferentDistances_whenProcessRequests_thenSortedAndSatisfied() {
        // Given
        List<TradeProposal> tradeRequests = new ArrayList<>();
        tradeRequests.add(new TradeProposal(Resource.AZENE, -5, 1.0f, 15.0, mockAgent, mockAgent));
        tradeRequests.add(new TradeProposal(Resource.BOGD, -10, 1.0f, 5.0, mockAgent, mockAgent));
        tradeRequests.add(new TradeProposal(Resource.CARBOL, -2, 1.0f, 10.0, mockAgent, mockAgent));
        
        // When
        uniteProduction.processRequests(tradeRequests);
        
        // Then
        // Verify sorting happened first (closest distance first)
        assertThat(tradeRequests.size()).isEqualTo(2);
        assertThat(tradeRequests.get(0).getDistance()).isEqualTo(5.0);
        assertThat(tradeRequests.get(1).getDistance()).isEqualTo(15.0);
        
        // Verify stock was updated appropriately
        assertThat(uniteProduction.stock.get(Resource.AZENE).getValue()).isEqualTo(5); // 10 - 5
        assertThat(uniteProduction.stock.get(Resource.BOGD).getValue()).isEqualTo(5); // 15 - 10
        assertThat(uniteProduction.stock.get(Resource.CARBOL).getValue()).isZero(); // 0 - 0
    }
}
