package br.com.mobhillmotos.api.adapter.out.persistence.moto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.mobhillmotos.api.domain.Moto;
import br.com.mobhillmotos.api.domain.MotoType;
import br.com.mobhillmotos.api.domain.MotoUpload;
import br.com.mobhillmotos.api.domain.exception.MotoNotFoundException;
import br.com.mobhillmotos.api.port.MotoRepository;

@Component
@Transactional
public class MotoJpaAdapter implements MotoRepository {

	private final MotoJpaRepository motoJpaRepository;

	public MotoJpaAdapter(MotoJpaRepository motoJpaRepository) {
		this.motoJpaRepository = motoJpaRepository;
	}

	@Override
	public Moto save(Moto moto) {
		MotoJpa saved = motoJpaRepository.save(toJpa(moto));
		return toDomain(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public Moto getById(Long motoId) {
		return motoJpaRepository.findById(motoId)
				.map(this::toDomain)
				.orElseThrow(() -> new MotoNotFoundException(motoId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Moto> listMoto(Boolean active, MotoType type) {
		return motoJpaRepository.search(active, type).stream().map(this::toDomain).toList();
	}

	@Override
	public void delete(Long motoId) {
		motoJpaRepository.deleteById(motoId);
	}

	private MotoJpa toJpa(Moto moto) {
		MotoJpa motoJpa = MotoJpa.builder()
				.id(moto.getId())
				.model(moto.getModel())
				.description(moto.getDescription())
				.kilometers(moto.getKilometers())
				.year(moto.getYear())
				.active(moto.getActive())
				.type(moto.getType())
				.badges(moto.getBadges() == null ? new ArrayList<>() : new ArrayList<>(moto.getBadges()))
				.images(new ArrayList<>())
				.build();

		if (moto.getImages() != null) {
			moto.getImages().forEach(image -> motoJpa.getImages().add(MotoUploadJpa.builder()
					.id(image.getId())
					.url(image.getUrl())
					.position(image.getPosition())
					.main(image.getMain())
					.moto(motoJpa)
					.build()));
		}
		return motoJpa;
	}

	private Moto toDomain(MotoJpa motoJpa) {
		List<MotoUpload> images = motoJpa.getImages().stream()
				.map(i -> MotoUpload.builder()
						.id(i.getId())
						.url(i.getUrl())
						.position(i.getPosition())
						.main(i.getMain())
						.build())
				.toList();

		return Moto.builder()
				.id(motoJpa.getId())
				.model(motoJpa.getModel())
				.description(motoJpa.getDescription())
				.kilometers(motoJpa.getKilometers())
				.year(motoJpa.getYear())
				.active(motoJpa.getActive())
				.type(motoJpa.getType())
				.badges(motoJpa.getBadges() == null ? List.of() : List.copyOf(motoJpa.getBadges()))
				.images(images)
				.build();
	}
}
