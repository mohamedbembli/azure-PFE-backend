package tn.dymes.store.services;

import java.io.IOException;

public interface IProductImageService {

    public byte[] getPublicPhoto(long productImageId) throws IOException;
    public void deleteProductImagesByProductId(long productId);
}
