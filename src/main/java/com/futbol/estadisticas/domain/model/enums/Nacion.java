package com.futbol.estadisticas.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Nacion {
    ARGENTINA("Argentina", "ARG"),
    ESPAÑA("España", "ESP"),
    FRANCIA("Francia", "FRA"),
    INGLATERRA("Inglaterra", "ENG"),
    PORTUGAL("Portugal", "POR"),
    BRASIL("Brasil", "BRA"),
    PAISES_BAJOS("Países Bajos", "NED"),
    ITALIA("Italia", "ITA"),
    CROACIA("Croacia", "CRO"),
    URUGUAY("Uruguay", "URU"),
    BELGICA("Bélgica", "BEL"),
    ALEMANIA("Alemania", "GER"),
    MARRUECOS("Marruecos", "MAR"),
    COLOMBIA("Colombia", "COL"),
    MEXICO("México", "MEX"),
    SUIZA("Suiza", "SUI"),
    ESTADOS_UNIDOS("Estados Unidos", "USA"),
    DINAMARCA("Dinamarca", "DEN"),
    JAPON("Japón", "JPN"),
    SENEGAL("Senegal", "SEN"),
    SUECIA("Suecia", "SWE"),
    POLONIA("Polonia", "POL"),
    IRAN("Irán", "IRN"),
    COREA_DEL_SUR("Corea del Sur", "KOR"),
    UCRANIA("Ucrania", "UKR"),
    SERBIA("Serbia", "SRB"),
    AUSTRALIA("Australia", "AUS"),
    ECUADOR("Ecuador", "ECU"),
    CHILE("Chile", "CHI"),
    AUSTRIA("Austria", "AUT"),
    TURQUIA("Turquía", "TUR"),
    NIGERIA("Nigeria", "NGA"),
    REPUBLICA_CHECA("República Checa", "CZE"),
    EGIPTO("Egipto", "EGY"),
    HUNGRIA("Hungría", "HUN"),
    CAMERUN("Camerún", "CMR"),
    ESCOCIA("Escocia", "SCO"),
    NORUEGA("Noruega", "NOR"),
    RUSIA("Rusia", "RUS"),
    COSTA_RICA("Costa Rica", "CRC"),
    GRECIA("Grecia", "GRE"),
    PARAGUAY("Paraguay", "PAR"),
    CANADA("Canadá", "CAN"),
    RUMANIA("Rumanía", "ROU"),
    ARGELIA("Argelia", "ALG"),
    PANAMA("Panamá", "PAN"),
    VENEZUELA("Venezuela", "VEN"),
    PERU("Perú", "PER"),
    ESLOVAQUIA("Eslovaquia", "SVK"),
    ARABIA_SAUDITA("Arabia Saudita", "KSA"),
    MALI("Mali", "MLI"),
    GHANA("Ghana", "GHA"),
    IRLANDA("Irlanda", "IRL"),
    ISLANDIA("Islandia", "ISL"),
    BOSNIA_HERZEGOVINA("Bosnia y Herzegovina", "BIH"),
    SUDAFRICA("Sudáfrica", "RSA"),
    JAMAICA("Jamaica", "JAM"),
    BURKINA_FASO("Burkina Faso", "BFA"),
    UZBEKISTAN("Uzbekistán", "UZB"),
    BOLIVIA("Bolivia", "BOL"),
    HONDURAS("Honduras", "HON"),
    GEORGIA("Georgia", "GEO"),
    CONGO("Congo", "CGO"),
    SIRIA("Siria", "SYR"),
    MONTENEGRO("Montenegro", "MNE"),
    ALBANIA("Albania", "ALB"),
    GUINEA("Guinea", "GUI"),
    GABON("Gabón", "GAB"),
    ISRAEL("Israel", "ISR"),
    EMIRATOS_ARABES_UNIDOS("Emiratos Árabes Unidos", "UAE"),
    CHINA("China", "CHN"),
    OMAN("Omán", "OMA"),
    HAITI("Haití", "HAI"),
    TRINIDAD_Y_TOBAGO("Trinidad y Tobago", "TRI"),
    COREA_DEL_NORTE("Corea del Norte", "PRK"),
    LUXEMBURGO("Luxemburgo", "LUX"),
    GUINEA_BISAU("Guinea-Bisáu", "GNB"),
    MADAGASCAR("Madagascar", "MAD"),
    BENIN("Benín", "BEN"),
    UGANDA("Uganda", "UGA"),
    ZAMBIA("Zambia", "ZAM"),
    JORDANIA("Jordania", "JOR"),
    BAHREIN("Bahréin", "BHR"),
    CURAZAO("Curazao", "CUW"),
    FINLANDIA("Finlandia", "FIN"),
    NUEVA_ZELANDA("Nueva Zelanda", "NZL"),
    ARMENIA("Armenia", "ARM"),
    GUINEA_ECUATORIAL("Guinea Ecuatorial", "EQG"),
    TANZANIA("Tanzania", "TAN"),
    MOZAMBIQUE("Mozambique", "MOZ"),
    NIGER("Níger", "NIG"),
    ETIOPIA("Etiopía", "ETH"),
    TOGO("Togo", "TOG"),
    NAMIBIA("Namibia", "NAM");
    
    private final String displayName;
    private final String codigoFIFA;

    @JsonCreator
    public static Nacion fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String normalized = value.trim().toUpperCase();

        for (Nacion nacion : Nacion.values()) {
            if (nacion.name().equals(normalized)) {
                return nacion;
            }
        }

        for (Nacion nacion : Nacion.values()) {
            if (nacion.getDisplayName().toUpperCase().equals(normalized)) {
                return nacion;
            }
        }

        for (Nacion nacion : Nacion.values()) {
            if (nacion.getCodigoFIFA().equalsIgnoreCase(normalized)) {
                return nacion;
            }
        }

        throw new IllegalArgumentException("Nacionalidad no válida: " + value);
    }

    public static Nacion fromCodigoFIFA(String codigoFIFA) {
        for (Nacion nacion : values()) {
            if (nacion.getCodigoFIFA().equalsIgnoreCase(codigoFIFA)) {
                return nacion;
            }
        }
        return null;
    }
    
    //Obtiene el país a partir del nombre de visualización
    public static Nacion fromDisplayName(String displayName) {
        for (Nacion nacion : values()) {
            if (nacion.getDisplayName().equalsIgnoreCase(displayName)) {
                return nacion;
            }
        }
        return null;
    }
    
    //Obtiene el nombre completo del país con su código FIFA
    public String getNombreConCodigo() {
        return String.format("%s (%s)", displayName, codigoFIFA);
    }
    
    @Override
    public String toString() {
        return getNombreConCodigo();
    }
}