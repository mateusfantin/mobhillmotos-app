package br.com.mobhillmotos.api.port;

import java.io.InputStream;

/** Arquivo de imagem independente de framework web. O stream é fechado pelo adapter. */
public record ImageFile(String fileName, String contentType, long size, InputStream content) {
}
