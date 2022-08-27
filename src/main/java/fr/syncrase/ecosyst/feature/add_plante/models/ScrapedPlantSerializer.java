package fr.syncrase.ecosyst.feature.add_plante.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.syncrase.ecosyst.domain.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

public class ScrapedPlantSerializer extends StdSerializer<ScrapedPlant> {

    private JsonGenerator gen;

    public ScrapedPlantSerializer() {
        this(null);
    }

    public ScrapedPlantSerializer(Class<ScrapedPlant> t) {
        super(t);
    }

    @Override
    public void serialize(ScrapedPlant scrapedPlant, JsonGenerator gen, SerializerProvider provider) throws IOException {
        this.gen = gen;
        gen.writeStartObject();
        writePlante(scrapedPlant);
        writeCronquistClassification(scrapedPlant);
        gen.writeEndObject();
    }

    private void writePlante(@NotNull ScrapedPlant scrapedPlant) throws IOException {
        gen.writeFieldName("plante");
        gen.writeStartObject();
        Plante plante = scrapedPlant.getPlante();
        writeNumber("id", plante.getId());
        writeNomsVernaculaires(plante.getNomsVernaculaires());
        writeReferences(plante.getReferences());
        gen.writeEndObject();
    }

    private void writeCronquistClassification(@NotNull ScrapedPlant scrapedPlant) throws IOException {
        gen.writeFieldName("cronquistClassificationBranch");
        gen.writeStartArray();
        for (CronquistRank cronquistRank : scrapedPlant.getCronquistClassificationBranch()) {
            gen.writeStartObject();
            writeNumber("id", cronquistRank.getId());
            gen.writeStringField("nom", cronquistRank.getNom());
            gen.writeStringField("rank", cronquistRank.getRank().name());
            if (cronquistRank.getClassification() != null) {
                writeClassification(cronquistRank.getClassification());
            }
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }

    private void writeClassification(@NotNull Classification classification) throws IOException {
        gen.writeFieldName("classification");
        gen.writeStartObject();
        writeNumber("id", classification.getId());
        // Pour l'instant pas de serialisation des classifications
        gen.writeEndObject();
    }

    private void writeNomsVernaculaires(@NotNull Set<NomVernaculaire> nomsVernaculaires) throws IOException {
        gen.writeFieldName("nomsVernaculaires");
        gen.writeStartArray();
        for (NomVernaculaire nv : nomsVernaculaires) {
            gen.writeStartObject();
            writeNumber("id", nv.getId());
            gen.writeStringField("nom", nv.getNom());
            writeString("description", nv.getDescription());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }

    private void writeReferences(@NotNull Set<Reference> references) throws IOException {
        gen.writeFieldName("references");
        gen.writeStartArray();
        for (Reference ref : references) {
            gen.writeStartObject();
            writeNumber("id", ref.getId());
            gen.writeStringField("type", ref.getType().toString());
            writeString("description", ref.getDescription());

            writeUrl(ref);

            gen.writeEndObject();
        }
        gen.writeEndArray();
    }

    private void writeUrl(@NotNull Reference ref) throws IOException {
        gen.writeFieldName("url");
        gen.writeStartObject();
        writeNumber("id", ref.getUrl().getId());
        gen.writeStringField("url", ref.getUrl().getUrl());
        gen.writeEndObject();
    }

    private void writeString(String fieldName, String nv) throws IOException {
        if (nv != null) {
            gen.writeStringField(fieldName, nv);
        } else {
            gen.writeNullField(fieldName);
        }
    }

    private void writeNumber(String fieldName, Long ref) throws IOException {
        if (ref != null) {
            gen.writeNumberField(fieldName, ref);
        } else {
            gen.writeNullField(fieldName);
        }
    }

}
