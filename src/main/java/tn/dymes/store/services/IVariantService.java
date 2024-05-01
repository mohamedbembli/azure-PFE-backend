package tn.dymes.store.services;

import java.io.IOException;

public interface IVariantService {
    byte[] getPublicPhoto(long variantId) throws IOException;

}
