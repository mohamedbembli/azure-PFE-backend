package tn.dymes.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tn.dymes.store.configuration.RsaKeysConfig;
import tn.dymes.store.entites.OrderLifeCycle;
import tn.dymes.store.entites.Store;
import tn.dymes.store.entites.User;
import tn.dymes.store.enums.Gender;
import tn.dymes.store.repositories.OrderLifeCycleRepository;
import tn.dymes.store.repositories.StoreRepository;
import tn.dymes.store.services.ICheckoutFieldService;
import tn.dymes.store.services.IRoleService;
import tn.dymes.store.services.IUserService;

import java.util.Date;
import java.util.UUID;

@EnableJpaRepositories("tn.dymes.store.repositories")
@EnableConfigurationProperties(RsaKeysConfig.class)
@SpringBootApplication
//EnableWebMvc
public class DymesApplication {
	@Autowired
	IUserService userService;
	@Autowired
	OrderLifeCycleRepository orderLifeCycleRepository;
	@Autowired
	IRoleService roleService;

	@Autowired
	ICheckoutFieldService checkoutFieldService;

	@Autowired
	StoreRepository storeRepository;

	public static void main(String[] args) {
		SpringApplication.run(DymesApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	void start() {
		// add Roles to DB
		if (this.roleService.retrieveRole("ADMIN") == null)
			this.roleService.addNewRole("ADMIN");
		if (this.roleService.retrieveRole("EMPLOYEE") == null)
			this.roleService.addNewRole("EMPLOYEE");
		if (this.roleService.retrieveRole("USER") == null)
			this.roleService.addNewRole("USER");
		// add employee Autorizations to DB

		// add all system roles
		// add admin
		User admin = this.userService.findUserByEmail("tn.dymes@gmail.com");
		if (admin == null) {
			User usr = new User();
			usr.setId(UUID.randomUUID().toString());
			System.out.println("user id = "+usr.getId());
			usr.setGender(Gender.valueOf("MALE"));
			usr.setCreate_account_date(new Date());
			// YYYY-MM-DD
			usr.setDob("1994-01-12");
			usr.setCity("Ksar Hellal");
			usr.setState("Monastir");
			usr.setAddress("1er étage, Imm Zitouna, Av. Hadj Ali Soua");
			usr.setPhone("50 415 444");
			usr.setZipCode("5070");
			usr.setCountry("Tunisia");
			usr.setEmail("tn.dymes@gmail.com");
			usr.setFirstName("Mohamed");
			usr.setLastName("Bembli");
			usr.setBio("IT DEV Dymes Store");
			usr.setPassword("1122");
			usr.setProfilePhoto(null);
			this.userService.addUser(usr);
			this.roleService.addRoleToUser(usr.getId(), "ADMIN");
			// add store
			Store store = new Store();
			store.setEntreprise("Dymes");
			store.setName("Dymes Store");
			store.setEmail(usr.getEmail());
			store.setCity(usr.getCity());
			store.setState(usr.getState());
			store.setPays(usr.getCountry());
			store.setZipCode(usr.getZipCode());
			store.setAddress(usr.getAddress());
			store.setPhone1(usr.getPhone());
			// PAY METHODES
			store.setPayWithBank(false);
			store.setPayInDelivery(true);
			storeRepository.save(store);
			//SHIPPING
			store.setGlobalDiscountShipping((float)300);
			// add checkoutForm data
			checkoutFieldService.addCheckoutField("Nom et prenom",true,true);
			checkoutFieldService.addCheckoutField("Pays",false,false);
			checkoutFieldService.addCheckoutField("Adresse",true,false);
			checkoutFieldService.addCheckoutField("Code TVA",false,false);
			checkoutFieldService.addCheckoutField("Région",true,true);
			checkoutFieldService.addCheckoutField("Ville",true,true);
			checkoutFieldService.addCheckoutField("Code postal",true,false);
			checkoutFieldService.addCheckoutField("Téléphone",true,true);
			checkoutFieldService.addCheckoutField("Téléphone supplémentaire",true,false);
			checkoutFieldService.addCheckoutField("Email",true,false);
			checkoutFieldService.addCheckoutField("Créer un compte",true,false);
			checkoutFieldService.addCheckoutField("Commentaire",true,false);
			// add ordersLifeCycles
			OrderLifeCycle pending = new OrderLifeCycle();
			pending.setStepName("En attente");
			pending.setStatus(true);
			pending.setPosition(1);
			pending.setAction("0");
			pending.setLogo("unique_343a37cf-5eff-4890-8ebf-dff092986b62.png");
			orderLifeCycleRepository.save(pending);

			OrderLifeCycle cofirmed = new OrderLifeCycle();
			cofirmed.setStepName("Confirmée");
			cofirmed.setStatus(true);
			cofirmed.setPosition(2);
			cofirmed.setAction("0");
			cofirmed.setLogo("unique_0ba01848-1539-4ea5-a4b8-86cda375c2d5.png");
			orderLifeCycleRepository.save(cofirmed);

			OrderLifeCycle ReadyToShip = new OrderLifeCycle();
			ReadyToShip.setStepName("Prêt à expédier");
			ReadyToShip.setStatus(true);
			ReadyToShip.setPosition(3);
			ReadyToShip.setAction("0");
			ReadyToShip.setLogo("unique_d8c24281-c22c-4d8f-b7ff-86a25f0c377a.png");
			orderLifeCycleRepository.save(ReadyToShip);

			OrderLifeCycle shipped = new OrderLifeCycle();
			shipped.setStepName("Expédiée");
			shipped.setStatus(true);
			shipped.setPosition(4);
			shipped.setAction("0");
			shipped.setLogo("unique_a12b0060-f6b1-4399-ae14-2ec88e40fa6f.png");
			orderLifeCycleRepository.save(shipped);

			OrderLifeCycle delivered = new OrderLifeCycle();
			delivered.setStepName("Livrée");
			delivered.setStatus(true);
			delivered.setPosition(5);
			delivered.setAction("0");
			delivered.setLogo("unique_5a8a81de-4cbe-4328-a80c-0efebf718caa.png");
			orderLifeCycleRepository.save(delivered);

			OrderLifeCycle returnNotReceived = new OrderLifeCycle();
			returnNotReceived.setStepName("Retour non reçue");
			returnNotReceived.setStatus(true);
			returnNotReceived.setPosition(6);
			returnNotReceived.setAction("1");
			returnNotReceived.setLogo("unique_f6050208-198c-4db6-b5a1-1316c0020630.png");
			orderLifeCycleRepository.save(returnNotReceived);

			OrderLifeCycle returnReceived = new OrderLifeCycle();
			returnReceived.setStepName("Retour reçue");
			returnReceived.setStatus(true);
			returnReceived.setPosition(7);
			returnReceived.setAction("1");
			returnReceived.setLogo("unique_b2b18f8d-923f-4fd1-8613-8ae8c31e63cf.png");
			orderLifeCycleRepository.save(returnReceived);

			OrderLifeCycle canceled = new OrderLifeCycle();
			canceled.setStepName("Annulée");
			canceled.setStatus(true);
			canceled.setPosition(8);
			canceled.setAction("1");
			canceled.setLogo("unique_8fcc79bc-04a8-41d1-b62c-d304ae744172.png");
			orderLifeCycleRepository.save(canceled);


		}


	}

}
