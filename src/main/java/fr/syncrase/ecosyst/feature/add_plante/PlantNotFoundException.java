package fr.syncrase.ecosyst.feature.add_plante;

public class PlantNotFoundException extends Throwable {
    public PlantNotFoundException(String name) {
        super("The plant " + name + " wasn't found on wikipedia");
    }
}
