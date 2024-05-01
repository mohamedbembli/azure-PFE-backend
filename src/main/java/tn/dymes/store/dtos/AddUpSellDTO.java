package tn.dymes.store.dtos;

public record AddUpSellDTO(

        String upsellID,
        String upSellName,
        String headerType,
        String headerContent,
        String bodyType,
        String bodyContent,
        String footerType,
        String footerContent,
        Long productID,
        Long nextProductID,
        String buttonsPosition,
        String btnConfirmText,
        String btnConfirmTextColor,
        String btnConfirmColor,
        String btnConfirmSize,
        String btnCancelText,
        String btnCancelTextColor,
        String btnCancelColor,
        String btnCancelSize,
        String creationDate

        ) {}
