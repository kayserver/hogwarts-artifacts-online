package edu.tcu.cs.hogwartsartifactsonline.wizard;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp() {
        this.wizards = new ArrayList<>();

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Harry Potter");
        this.wizards.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Hermione Granger");
        this.wizards.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Ron Weasley");
        this.wizards.add(w3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        // Given
        Wizard wizard = this.wizards.get(0);
        given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));

        // When
        Wizard returnedWizard = this.wizardService.findById(1);

        // Then
        assertThat(returnedWizard.getId()).isEqualTo(wizard.getId());
        assertThat(returnedWizard.getName()).isEqualTo(wizard.getName());
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> {
            this.wizardService.findById(1);
        });

        // Then
        assertThat(thrown).isInstanceOf(WizardNotFoundException.class);
        assertThat(thrown.getMessage()).isEqualTo("Could not find wizard with Id 1 :(");
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindAllSuccess() {
        // Given
        given(this.wizardRepository.findAll()).willReturn(this.wizards);

        // When
        List<Wizard> returnedWizards = this.wizardService.findAll();

        // Then
        assertThat(returnedWizards.size()).isEqualTo(this.wizards.size());
        verify(this.wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        // Given
        Wizard newWizard = new Wizard();
        newWizard.setId(4);
        newWizard.setName("Draco Malfoy");

        given(this.wizardRepository.save(newWizard)).willReturn(newWizard);

        // When
        Wizard savedWizard = this.wizardService.save(newWizard);

        // Then
        assertThat(savedWizard.getId()).isEqualTo(newWizard.getId());
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        verify(this.wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Wizard existingWizard = this.wizards.get(0);
        existingWizard.setId(1);
        existingWizard.setName("Harry Potter");

        Wizard updateWizard = new Wizard();
        updateWizard.setName("Harry James Potter");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(existingWizard));
        given(this.wizardRepository.save(existingWizard)).willReturn(existingWizard);

        // When
        Wizard updatedWizard = this.wizardService.update(1, updateWizard);

        // Then
        assertThat(updatedWizard.getId()).isEqualTo(1);
        assertThat(updatedWizard.getName()).isEqualTo("Harry James Potter");
        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, times(1)).save(existingWizard);
    }

    @Test
    void testUpdateNotFound() {
        // Given
        Wizard updateWizard = new Wizard();
        updateWizard.setName("Harry James Potter");

        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> {
            this.wizardService.update(1, updateWizard);
        });

        // Then
        assertThat(thrown).isInstanceOf(WizardNotFoundException.class);
        assertThat(thrown.getMessage()).isEqualTo("Could not find wizard with Id 1 :(");
        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, never()).save(any(Wizard.class));
    }

    @Test
    void testDeleteSuccess() {
        // Given
        Wizard wizard = this.wizards.get(0);
        given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));
        doNothing().when(this.wizardRepository).delete(wizard);

        // When
        this.wizardService.delete(1);

        // Then
        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, times(1)).delete(wizard);
    }

    @Test
    void testDeleteNotFound() {
        // Given
        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> {
            this.wizardService.delete(1);
        });

        // Then
        assertThat(thrown).isInstanceOf(WizardNotFoundException.class);
        assertThat(thrown.getMessage()).isEqualTo("Could not find wizard with Id 1 :(");
        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, never()).delete(any(Wizard.class));
    }
}