package handlers.communityboard.custom.bufflists;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Printer {

    public static void main(String[] args) {


        System.out.println(String.join(";",
                Stream.of(new Dominator().getBuffs(), new Doomcryer().getBuffs(), new ElvenSaint().getBuffs(),
                        new Hierophant().getBuffs(), new Maestro().getBuffs(), new ShillenSaint().getBuffs(),
                        new SpectralDancer().getBuffs(), new Summoners().getBuffs(), new SwordMuse().getBuffs())
                        .flatMap(buffs -> buffs.stream().map(skillHolder -> skillHolder.getSkillId() + ",7200"))
                        .collect(Collectors.toList())));


    }

}
