package com.example.hibernateonetoonedemo.bootstrap;

import com.example.hibernateonetoonedemo.repository.HusbandRepository;
import com.example.hibernateonetoonedemo.repository.WifeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataRunner implements CommandLineRunner {

    private final HusbandRepository husbandRepository;
    private final WifeRepository wifeRepository;

    public DataRunner(HusbandRepository husbandRepository, WifeRepository wifeRepository) {
        this.husbandRepository = husbandRepository;
        this.wifeRepository = wifeRepository;
    }

    @Override
    public void run(String... args) {
        wifeRepository.deleteAll();
        husbandRepository.deleteAll();

        // Husband h1 = new Husband("Ravi");
        // Wife w1 = new Wife("Sita");
        // h1.setWife(w1);
        // Husband savedHusband = husbandRepository.save(h1);
        // Long wifeId = savedHusband.getWife() != null ? savedHusband.getWife().getId() : null;
        // System.out.println("Saved Husband: " + savedHusband.getName() + ", wifeId=" + wifeId);

        // Husband h2 = new Husband("Arjun");
        // Wife w2 = new Wife("Meera");
        // w2.setHusband(h2);
        // Wife savedWife = wifeRepository.save(w2);
        // Long husbandId = savedWife.getHusband() != null ? savedWife.getHusband().getId() : null;
        // System.out.println("Saved Wife: " + savedWife.getName() + ", husbandId=" + husbandId);
    }
}
