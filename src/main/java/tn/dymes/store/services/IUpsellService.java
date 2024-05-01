package tn.dymes.store.services;

import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddUpSellDTO;
import tn.dymes.store.entites.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public interface IUpsellService {
    void addUpsell(AddUpSellDTO addUpSellDTO, List<MultipartFile> imagesList) throws IOException;

    void updateUpsell(long upsellID, AddUpSellDTO addUpSellDTO, List<MultipartFile> imagesList) throws IOException;
    boolean isExist(String name);

    MultipartFile processImages(List<MultipartFile> imagesList, String keyword);

    Optional<MultipartFile> findImageByName(List<MultipartFile> imagesList, String keyword);
    void changeStatus(long upsellID, boolean status);

    byte[] getPublicPhoto(long upsellID, String position) throws IOException;

    void deleteUpSell(long upsellID);

    void cancelUpsell(long upsellID);

    void approveUpsell(long upsellID);

}
