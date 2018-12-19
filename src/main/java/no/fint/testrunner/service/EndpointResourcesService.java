package no.fint.testrunner.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class EndpointResourcesService {

    private Map<String, List<String>> resources;

    @PostConstruct
    private void init() {
        resources = new HashMap<>();

        resources.put("/administrasjon/personal",
                Arrays.asList("person", "personalressurs", "arbeidsforhold", "fravar", "fastlonn", "variabellonn", "fasttillegg"));
        resources.put("/administrasjon/fullmakt",
                Arrays.asList("fullmakt", "rolle"));
        resources.put("/administrasjon/kodeverk",
                Arrays.asList(
                        "uketimetall",
                        "ansvar",
                        "stillingskode",
                        "personalressurskategori",
                        "prosjekt",
                        "art",
                        "funksjon",
                        "fravarstype",
                        "lonnsart",
                        "fravarsgrunn",
                        "arbeidsforholdstype"
                ));
        resources.put("/administrasjon/organisasjon",
                Collections.singletonList("organisasjonselement"));

        resources.put("/utdanning/vurdering",
                Arrays.asList("eksamensgruppe", "vurdering", "fravar", "karakterverdi"));
        resources.put("/utdanning/utdanningsprogram",
                Arrays.asList("arstrinn", "utdanningsprogram", "programomrade", "skole"));
        resources.put("/utdanning/timeplan",
                Arrays.asList("fag", "undervisningsgruppe", "time", "rom"));
        resources.put("/utdanning/kodeverk",
                Arrays.asList("fravarstype", "karakterskala", "skoleeiertype", "elevkategori"));
        resources.put("/utdanning/elev",
                Arrays.asList(
                        "person",
                        "elev",
                        "elevforhold",
                        "undervisningsforhold",
                        "skoleressurs",
                        "medlemskap",
                        "basisgruppe",
                        "kontaktlarergruppe"
                ));

        resources.put("/utdanning/vigo/kodeverk",
                Arrays.asList(
                        "eksamensformer",
                        "eksamensvurderinger",
                        "fag",
                        "fagmerknader",
                        "fagtyper",
                        "fremmedsprak",
                        "fylker",
                        "hovedomrader",
                        "karakterer",
                        "kommuner",
                        "kompetansemal",
                        "kompetansemalsett",
                        "land",
                        "lareplaner",
                        "merkelapper",
                        "morsmal",
                        "malform",
                        "opplaringsfag",
                        "poststeder",
                        "programomradekategorier",
                        "programomrader",
                        "skoler",
                        "utdanningsprogrammer",
                        "variablerregistreringshandboken",
                        "vitnemalsmerknader",
                        "onskestatus",
                        "arsakskoder")
        );
    }

    public Optional<List<String>> getEndpointResources(String endpoint) {

        return Optional.ofNullable(resources.get(endpoint));
    }
}
