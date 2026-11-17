package feature.components.InventoryComponentTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import feature.inventory.Item;
import feature.inventory.items.ItemPotionHealth;
import feature.inventory.items.ItemPotionWater;
import feature.components.InventoryComponent;

public class AddItemTest {
    
    private Item mockItemPotionHealth1;
    private Item mockItemPotionHealth2;
    private Item mockItemPotionWater;

    @BeforeEach 
    void setUp() {
        this.mockItemPotionHealth1 = mock(ItemPotionHealth.class);
        when(this.mockItemPotionHealth1.stackSize()).thenReturn(5);
        when(this.mockItemPotionHealth1.maxStackSize()).thenReturn(7);
        this.mockItemPotionHealth2 = mock(ItemPotionHealth.class);
        when(this.mockItemPotionHealth2.stackSize()).thenReturn(5);
        when(this.mockItemPotionHealth2.maxStackSize()).thenReturn(7);
        this.mockItemPotionWater = mock(ItemPotionWater.class);
        when(this.mockItemPotionWater.stackSize()).thenReturn(5);
    }
    
    @Test 
    void test_ItemPotionHealthSize5StackMaxSize5_ItemAddedToInventory(){
        InventoryComponent iComponent = new InventoryComponent(5);
        
        iComponent.add(mockItemPotionHealth1);

        assertEquals(iComponent.items()[0], mockItemPotionHealth1);
    }

    @Test 
    void test_ItemPotionWaterSize5StackMaxSize10ItemPotionHealthIncludet_ItemAddedToInventory(){
        InventoryComponent iComponent = new InventoryComponent(10);

        iComponent.add(mockItemPotionHealth1);
        iComponent.add(mockItemPotionWater);

        assertEquals(iComponent.items()[1], mockItemPotionWater);
    }

    @Test 
    void test(){
        InventoryComponent iComponent = new InventoryComponent(15);

        iComponent.add(mockItemPotionHealth1);
        iComponent.add(mockItemPotionWater);
        iComponent.add(mockItemPotionHealth2);

        //auftrennen
        Mockito.verify(mockItemPotionHealth1, Mockito.times(1)).stackSize((byte)7);
        Mockito.verify(mockItemPotionHealth2 ,Mockito.times(1)).stackSize((byte) 3);
        assertEquals(iComponent.items()[2], mockItemPotionHealth2);
    }
}
