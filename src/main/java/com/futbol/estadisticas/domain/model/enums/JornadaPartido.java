package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JornadaPartido {

        JORNADA_1("Jornada 1"),
        JORNADA_2("Jornada 2"),
        JORNADA_3("Jornada 3"),
        JORNADA_4("Jornada 4"),
        JORNADA_5("Jornada 5"),
        JORNADA_6("Jornada 6"),
        JORNADA_7("Jornada 7"),
        JORNADA_8("Jornada 8"),
        JORNADA_9("Jornada 9"),
        JORNADA_10("Jornada 10"),
        JORNADA_11("Jornada 11"),
        JORNADA_12("Jornada 12"),
        JORNADA_13("Jornada 13"),
        JORNADA_14("Jornada 14"),
        JORNADA_15("Jornada 15"),
        JORNADA_16("Jornada 16"),
        JORNADA_17("Jornada 17"),
        JORNADA_18("Jornada 18"),
        JORNADA_19("Jornada 19"),
        JORNADA_20("Jornada 20"),
        JORNADA_21("Jornada 21"),
        JORNADA_22("Jornada 22"),
        JORNADA_23("Jornada 23"),
        JORNADA_24("Jornada 24"),
        JORNADA_25("Jornada 25"),
        JORNADA_26("Jornada 26"),
        JORNADA_27("Jornada 27"),
        JORNADA_28("Jornada 28"),
        JORNADA_29("Jornada 29"),
        JORNADA_30("Jornada 30"),
        JORNADA_31("Jornada 31"),
        JORNADA_32("Jornada 32"),
        JORNADA_33("Jornada 33"),
        JORNADA_34("Jornada 34"),
        JORNADA_35("Jornada 35"),
        JORNADA_36("Jornada 36"),
        JORNADA_37("Jornada 37"),
        JORNADA_38("Jornada 38"),
        IDA("Ida"),
        VUELTA("Vuelta"),
        UNICO("Partido Único");

        private final String displayName;

        @Override
        public String toString() {
                return getDisplayName();
        }
    }

