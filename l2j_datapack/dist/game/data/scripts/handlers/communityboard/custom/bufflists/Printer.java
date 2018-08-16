package handlers.communityboard.custom.bufflists;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Printer {

    public static void main(String[] args) {


        System.out.println(String.join(";",
                Stream.of(Dominator.BUFFS, Doomcryer.BUFFS, ElvenSaint.BUFFS,
                        Hierophant.BUFFS, Maestro.BUFFS, ShillenSaint.BUFFS,
                        SpectralDancer.BUFFS, Summoners.BUFFS, SwordMuse.BUFFS)
                        .flatMap(buffs -> buffs.stream().map(skillHolder -> skillHolder.getSkillId() + ",7200"))
                        .collect(Collectors.toList())));


    }

}
