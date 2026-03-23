package edu.tcu.cs.hogwartsartifactsonline.wizard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
    }

    public Wizard findById(Integer id) {
        return wizardRepository.findById(id)
                .orElseThrow(() -> new WizardNotFoundException(id));
    }

    public List<Wizard> findAll() {
        return wizardRepository.findAll();
    }

    public Wizard save(Wizard wizard) {
        return wizardRepository.save(wizard);
    }

    public Wizard update(Integer id, Wizard wizard) {
        Wizard foundWizard = this.findById(id);
        foundWizard.setName(wizard.getName());
        return wizardRepository.save(foundWizard);
    }

    public void delete(Integer id) {
        Wizard wizard = this.findById(id);
        wizardRepository.delete(wizard);
    }
}