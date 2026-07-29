package com.futbol.estadisticas.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Nacion {
    AFGANISTAN("Afganistán", "AFG"),
    ALBANIA("Albania", "ALB"),
    ALEMANIA("Alemania", "GER"),
    ANDORRA("Andorra", "AND"),
    ANGOLA("Angola", "ANG"),
    ARABIA_SAUDITA("Arabia Saudita", "KSA"),
    ARGELIA("Argelia", "ALG"),
    ARGENTINA("Argentina", "ARG"),
    ARMENIA("Armenia", "ARM"),
    AUSTRALIA("Australia", "AUS"),
    AUSTRIA("Austria", "AUT"),
    AZERBAIYAN("Azerbaiyán", "AZE"),
    BAHAMAS("Bahamas", "BAH"),
    BAHREIN("Bahréin", "BHR"),
    BANGLADESH("Bangladesh", "BAN"),
    BARBADOS("Barbados", "BRB"),
    BELGICA("Bélgica", "BEL"),
    BELICE("Belice", "BLZ"),
    BENIN("Benín", "BEN"),
    BIELORRUSIA("Bielorrusia", "BLR"),
    BOLIVIA("Bolivia", "BOL"),
    BOSNIA_HERZEGOVINA("Bosnia y Herzegovina", "BIH"),
    BOTSUANA("Botsuana", "BOT"),
    BRASIL("Brasil", "BRA"),
    BRUNEI("Brunéi", "BRU"),
    BULGARIA("Bulgaria", "BUL"),
    BURKINA_FASO("Burkina Faso", "BFA"),
    BURUNDI("Burundi", "BDI"),
    BUTAN("Bután", "BHU"),
    CABO_VERDE("Cabo Verde", "CPV"),
    CAMBODIA("Camboya", "CAM"),
    CAMERUN("Camerún", "CMR"),
    CANADA("Canadá", "CAN"),
    CATAR("Catar", "QAT"),
    CHAD("Chad", "CHA"),
    CHILE("Chile", "CHI"),
    CHINA("China", "CHN"),
    CHIPRE("Chipre", "CYP"),
    COLOMBIA("Colombia", "COL"),
    COMORAS("Comoras", "COM"),
    CONGO("Congo", "CGO"),
    CONGO_RD("Congo (Rep. Dem.)", "COD"),
    COREA_DEL_NORTE("Corea del Norte", "PRK"),
    COREA_DEL_SUR("Corea del Sur", "KOR"),
    COSTA_DE_MARFIL("Costa de Marfil", "CIV"),
    COSTA_RICA("Costa Rica", "CRC"),
    CROACIA("Croacia", "CRO"),
    CUBA("Cuba", "CUB"),
    CURAZAO("Curazao", "CUW"),
    DINAMARCA("Dinamarca", "DEN"),
    DOMINICA("Dominica", "DMA"),
    ECUADOR("Ecuador", "ECU"),
    EGIPTO("Egipto", "EGY"),
    EL_SALVADOR("El Salvador", "SLV"),
    EMIRATOS_ARABES_UNIDOS("Emiratos Árabes Unidos", "UAE"),
    ERITREA("Eritrea", "ERI"),
    ESLOVAQUIA("Eslovaquia", "SVK"),
    ESLOVENIA("Eslovenia", "SVN"),
    ESCOCIA("Escocia", "SCO"),
    ESPAÑA("España", "ESP"),
    ESTADOS_UNIDOS("Estados Unidos", "USA"),
    ESTONIA("Estonia", "EST"),
    ESWATINI("Eswatini", "SWZ"),
    ETIOPIA("Etiopía", "ETH"),
    FILIPINAS("Filipinas", "PHI"),
    FINLANDIA("Finlandia", "FIN"),
    FIJI("Fiyi", "FIJ"),
    FRANCIA("Francia", "FRA"),
    GABON("Gabón", "GAB"),
    GAMBIA("Gambia", "GAM"),
    GEORGIA("Georgia", "GEO"),
    GHANA("Ghana", "GHA"),
    GRANADA("Granada", "GRN"),
    GRECIA("Grecia", "GRE"),
    GUATEMALA("Guatemala", "GUA"),
    GUINEA("Guinea", "GUI"),
    GUINEA_BISAU("Guinea-Bisáu", "GNB"),
    GUINEA_ECUATORIAL("Guinea Ecuatorial", "EQG"),
    GUYANA("Guyana", "GUY"),
    HAITI("Haití", "HAI"),
    HONDURAS("Honduras", "HON"),
    HUNGRIA("Hungría", "HUN"),
    INDIA("India", "IND"),
    INDONESIA("Indonesia", "IDN"),
    INGLATERRA("Inglaterra", "ENG"),
    IRAN("Irán", "IRN"),
    IRAK("Irak", "IRQ"),
    IRLANDA("Irlanda", "IRL"),
    ISLANDIA("Islandia", "ISL"),
    ISLAS_MARSHALL("Islas Marshall", "MHL"),
    ISLAS_SALOMON("Islas Salomón", "SOL"),
    ISRAEL("Israel", "ISR"),
    ITALIA("Italia", "ITA"),
    JAMAICA("Jamaica", "JAM"),
    JAPON("Japón", "JPN"),
    JORDANIA("Jordania", "JOR"),
    KAZAJISTAN("Kazajistán", "KAZ"),
    KENIA("Kenia", "KEN"),
    KIRGUISTAN("Kirguistán", "KGZ"),
    KOSOVO("Kosovo", "KOS"),
    KUWAIT("Kuwait", "KUW"),
    LAOS("Laos", "LAO"),
    LESOTO("Lesoto", "LES"),
    LETONIA("Letonia", "LVA"),
    LIBANO("Líbano", "LIB"),
    LIBERIA("Liberia", "LBR"),
    LIBIA("Libia", "LBY"),
    LIECHTENSTEIN("Liechtenstein", "LIE"),
    LITUANIA("Lituania", "LTU"),
    LUXEMBURGO("Luxemburgo", "LUX"),
    MADAGASCAR("Madagascar", "MAD"),
    MALASIA("Malasia", "MAS"),
    MALAWI("Malawi", "MWI"),
    MALDIVAS("Maldivas", "MDV"),
    MALI("Mali", "MLI"),
    MALTA("Malta", "MLT"),
    MARRUECOS("Marruecos", "MAR"),
    MAURICIO("Mauricio", "MRI"),
    MAURITANIA("Mauritania", "MTN"),
    MEXICO("México", "MEX"),
    MICRONESIA("Micronesia", "FSM"),
    MOLDOVA("Moldova", "MDA"),
    MONACO("Mónaco", "MCO"),
    MONGOLIA("Mongolia", "MNG"),
    MONTENEGRO("Montenegro", "MNE"),
    MOZAMBIQUE("Mozambique", "MOZ"),
    MYANMAR("Myanmar", "MYA"),
    NAMIBIA("Namibia", "NAM"),
    NAURU("Nauru", "NRU"),
    NEPAL("Nepal", "NEP"),
    NICARAGUA("Nicaragua", "NCA"),
    NIGER("Níger", "NIG"),
    NIGERIA("Nigeria", "NGA"),
    NORUEGA("Noruega", "NOR"),
    NUEVA_ZELANDA("Nueva Zelanda", "NZL"),
    OMAN("Omán", "OMA"),
    PAISES_BAJOS("Países Bajos", "NED"),
    PAKISTAN("Pakistán", "PAK"),
    PALAU("Palaos", "PLW"),
    PALESTINA("Palestina", "PLE"),
    PANAMA("Panamá", "PAN"),
    PAPUA_NUEVA_GUINEA("Papúa Nueva Guinea", "PNG"),
    PARAGUAY("Paraguay", "PAR"),
    PERU("Perú", "PER"),
    POLONIA("Polonia", "POL"),
    PORTUGAL("Portugal", "POR"),
    REPUBLICA_CENTROAFRICANA("República Centroafricana", "CTA"),
    REPUBLICA_CHECA("República Checa", "CZE"),
    REPUBLICA_DOMINICANA("República Dominicana", "DOM"),
    RUMANIA("Rumanía", "ROU"),
    RUANDA("Ruanda", "RWA"),
    RUSIA("Rusia", "RUS"),
    SAMOA("Samoa", "SAM"),
    SAN_MARINO("San Marino", "SMR"),
    SANTA_LUCIA("Santa Lucía", "LCA"),
    SANTO_TOME_Y_PRINCIPE("Santo Tomé y Príncipe", "STP"),
    SENEGAL("Senegal", "SEN"),
    SERBIA("Serbia", "SRB"),
    SEYCHELLES("Seychelles", "SEY"),
    SIERRA_LEONA("Sierra Leona", "SLE"),
    SINGAPUR("Singapur", "SGP"),
    SIRIA("Siria", "SYR"),
    SOMALIA("Somalia", "SOM"),
    SRI_LANKA("Sri Lanka", "SRI"),
    SUDAFRICA("Sudáfrica", "RSA"),
    SUDAN("Sudán", "SDN"),
    SUDAN_DEL_SUR("Sudán del Sur", "SSD"),
    SUECIA("Suecia", "SWE"),
    SUIZA("Suiza", "SUI"),
    SURINAM("Surinam", "SUR"),
    TADYIKISTAN("Tayikistán", "TJK"),
    TANZANIA("Tanzania", "TAN"),
    TAILANDIA("Tailandia", "THA"),
    TIMOR_ORIENTAL("Timor Oriental", "TLS"),
    TOGO("Togo", "TOG"),
    TONGA("Tonga", "TGA"),
    TRINIDAD_Y_TOBAGO("Trinidad y Tobago", "TRI"),
    TUNEZ("Túnez", "TUN"),
    TURKMENISTAN("Turkmenistán", "TKM"),
    TURQUIA("Turquía", "TUR"),
    TUVALU("Tuvalu", "TUV"),
    UCRANIA("Ucrania", "UKR"),
    UGANDA("Uganda", "UGA"),
    URUGUAY("Uruguay", "URU"),
    UZBEKISTAN("Uzbekistán", "UZB"),
    VANUATU("Vanuatu", "VAN"),
    VENEZUELA("Venezuela", "VEN"),
    VIETNAM("Vietnam", "VIE"),
    YEMEN("Yemen", "YEM"),
    YIBUTI("Yibuti", "DJI"),
    ZAMBIA("Zambia", "ZAM"),
    ZIMBABUE("Zimbabue", "ZIM");
    
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