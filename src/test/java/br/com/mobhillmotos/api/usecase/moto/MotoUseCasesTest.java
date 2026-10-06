package br.com.mobhillmotos.api.usecase.moto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoType;
import br.com.mobhillmotos.api.domain.MotoUpload;
import br.com.mobhillmotos.api.domain.exception.MotoNotFoundException;
import br.com.mobhillmotos.api.port.MotoRepository;
import br.com.mobhillmotos.api.port.UploadImage;

class MotoUseCasesTest {

	private MotoRepository repository;
	private UploadImage uploadImage;

	private final MotoCommand command = new MotoCommand("CG 160", "Revisada", 2022, 12000, null, MotoType.USED,
			List.of(" Único dono ", "", "Revisada", "Revisada"),
			List.of(new MotoCommand.Image("/uploads/1.png", 1, true)));

	@BeforeEach
	void setUp() {
		repository = mock(MotoRepository.class);
		uploadImage = mock(UploadImage.class);
		when(repository.save(any(Moto.class))).thenAnswer(i -> i.getArgument(0));
	}

	@Test
	void createDefaultsActiveToTrueAndHasNoId() {
		Moto moto = new CreateMoto(repository).execute(command);

		assertEquals(null, moto.getId());
		assertTrue(moto.getActive());
		assertEquals(1, moto.getImages().size());
	}

	@Test
	void createCleansUpBadges() {
		Moto moto = new CreateMoto(repository).execute(command);

		assertEquals(List.of("Único dono", "Revisada"), moto.getBadges());
	}

	@Test
	void updateKeepsIdFromPath() {
		when(repository.getById(7L)).thenReturn(Moto.builder().id(7L).build());

		new UpdateMoto(repository, uploadImage).execute(7L, command);

		ArgumentCaptor<Moto> captor = ArgumentCaptor.forClass(Moto.class);
		verify(repository).save(captor.capture());
		assertEquals(7L, captor.getValue().getId());
	}

	@Test
	void updateFailsWhenMotoDoesNotExist() {
		when(repository.getById(9L)).thenThrow(new MotoNotFoundException(9L));

		assertThrows(MotoNotFoundException.class, () -> new UpdateMoto(repository, uploadImage).execute(9L, command));
		verify(repository, never()).save(any());
	}

	@Test
	void deleteFailsWhenMotoDoesNotExist() {
		when(repository.getById(9L)).thenThrow(new MotoNotFoundException(9L));

		assertThrows(MotoNotFoundException.class, () -> new DeleteMoto(repository, uploadImage).execute(9L));
		verify(repository, never()).delete(any());
	}

	@Test
	void updateDeletesImagesNoLongerUsed() {
		when(repository.getById(7L)).thenReturn(Moto.builder().id(7L).images(List.of(
				MotoUpload.builder().url("/uploads/1.png").build(),
				MotoUpload.builder().url("/uploads/antiga.png").build())).build());

		new UpdateMoto(repository, uploadImage).execute(7L, command);

		verify(uploadImage).delete("/uploads/antiga.png");
		verify(uploadImage, never()).delete("/uploads/1.png");
	}

	@Test
	void deleteRemovesMotoAndItsImages() {
		when(repository.getById(7L)).thenReturn(Moto.builder().id(7L).images(List.of(
				MotoUpload.builder().url("/uploads/1.png").build(),
				MotoUpload.builder().url("/uploads/2.png").build())).build());

		new DeleteMoto(repository, uploadImage).execute(7L);

		verify(repository).delete(7L);
		verify(uploadImage, times(2)).delete(any());
	}

	@Test
	void searchPassesFiltersToRepository() {
		new SearchMoto(repository).list(true, MotoType.NEW);

		verify(repository).listMoto(true, MotoType.NEW);
	}
}
