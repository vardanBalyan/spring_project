package com.ttn.bootcampProject;

import com.ttn.bootcampProject.entities.*;
import com.ttn.bootcampProject.entities.orders.Orders;
import com.ttn.bootcampProject.entities.products.Product;
import com.ttn.bootcampProject.entities.products.ProductReview;
import com.ttn.bootcampProject.entities.products.ProductVariation;
import com.ttn.bootcampProject.entities.products.categories.Category;
import com.ttn.bootcampProject.entities.products.categories.CategoryMetadataFieldValues;
import com.ttn.bootcampProject.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class BootcampProjectApplicationTests {

	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CategoryMetadataFieldRepository categoryMetadataFieldRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	ProductVariationRepository productVariationRepository;
	@Autowired
	ProductRepository productRepository;

	@Test
	void contextLoads() {
	}

	@Test
	public void testCreateUserAndAddAddress()
	{
		User user = new User();
		user.setEmail("gsayg@gmail.com");
		user.setFirstName("Vardan");
		user.setLastName("Balyan");
		user.setPassword("12345");
		user.setActive(true);
		user.setDeleted(false);

		Address address1 = new Address();
		address1.setAddressLine("WZ-234, junken street");
		address1.setCity("New Delhi");
		address1.setState("Delhi");
		address1.setCountry("India");
		address1.setLabel("None label available");
		address1.setZipCode("110097");

		Address address2 = new Address();
		address2.setAddressLine("R32, karol bagh");
		address2.setCity("New Delhi");
		address2.setState("Delhi");
		address2.setCountry("India");
		address2.setLabel("Rakuen");
		address2.setZipCode("110011");

		user.addAddress(address1);
		user.addAddress(address2);
		userRepository.save(user);
	}

	@Test
	public void testCreateUserAndSeller()
	{
		Seller seller = new Seller();
		seller.setFirstName("Parth");
		seller.setLastName("Choudhary");
		seller.setActive(true);
		seller.setDeleted(false);
		String pass = passwordEncoder.encode("12345");
		seller.setPassword(pass);
		seller.setMiddleName("Pawar");
		seller.setCompanyName("fgdgs");
		seller.setGst("2564356652");
		seller.setCompanyContact("35424141");

		userRepository.save(seller);
	}

	@Test
	public void testCreateUserAndCustomer()
	{
		Customer customer = new Customer();
		customer.setFirstName("Arun");
		customer.setLastName("Pawar");
		customer.setMiddleName("Singh");
		customer.setEmail("Arun@gmail.com");
		customer.setActive(false);
		customer.setDeleted(true);
		String pass = passwordEncoder.encode("12345");
		customer.setPassword(pass);
		customer.setContact("3525761182");


		userRepository.save(customer);
	}

	@Test
	public void testCreateUserAndRole()
	{
		User user = new User();
		user.setEmail("gsayg@gmail.com");
		user.setFirstName("Vardan");
		user.setLastName("Balyan");
		user.setPassword("12345");
		user.setActive(true);
		user.setDeleted(false);

		Role role = new Role();
		role.setAuthority("ADMIN");

		Role role2 = new Role();
		role2.setAuthority("USER");

		//user.addRoles(role);
		//user.addRoles(role2);
		userRepository.save(user);
	}

	@Test
	public void testAddSellerAndProducts()
	{

		Seller seller = new Seller();
		seller.setFirstName("Parth");
		seller.setLastName("Choudhary");
		seller.setActive(true);
		seller.setDeleted(false);
		String pass = passwordEncoder.encode("12345");
		seller.setPassword(pass);
		seller.setMiddleName("Pawar");
		seller.setCompanyName("fgdgs");
		seller.setGst("2564356652");
		seller.setCompanyContact("35424141");

		Product product = new Product();
		product.setActive(true);
		product.setBrand("OnePlus");
		product.setCancellable(true);
		product.setDescription("One plus 8T");
		product.setName("OnePlus mobile");
		product.setReturnable(false);

		seller.addProducts(product);

		//user.setSeller(seller);
		userRepository.save(seller);
	}

	@Test
	public void testCategory()
	{
		Category category = new Category();
		category.setName("Fashion");
		Category shirt = new Category("shirt");
		Category shoes = new Category("shoes");

		Set<Category> shirts = new HashSet<>();
		shirts.add(new Category("Casual"));
		shirts.add(new Category("Formal"));
		shirt.setCategorySet(shirts);

		Set<Category> shoesSet = new HashSet<>();
		shoesSet.add(new Category("sneakers"));
		shoesSet.add(new Category("sports"));
		shoes.setCategorySet(shoesSet);

		Set<Category> categories = new HashSet<>();
		categories.add(shirt);
		categories.add(shoes);
		category.setCategorySet(categories);

		categoryRepository.save(category);
	}

	@Test
	public void testCategoryMetadataFieldValuesTable()
	{
		Category category = new Category();
		category.setName("Fashion");
		Category shirt = new Category("shirt");
		Category shoes = new Category("shoes");

		Set<Category> shirts = new HashSet<>();
		shirts.add(new Category("Casual"));
		shirts.add(new Category("Formal"));
		shirt.setCategorySet(shirts);

		Set<Category> shoesSet = new HashSet<>();
		shoesSet.add(new Category("sneakers"));
		shoesSet.add(new Category("sports"));
		shoes.setCategorySet(shoesSet);

		Set<Category> categories = new HashSet<>();
		categories.add(shirt);
		categories.add(shoes);
		category.setCategorySet(categories);

		CategoryMetadataFieldValues cm = new CategoryMetadataFieldValues();
		cm.setValue("tasjhag");

		shirt.addCategoryMetadataFieldValues(cm);

		categoryRepository.save(category);
	}

	@Test
	public void testAddOrder()
	{
		Customer customer = new Customer();
		customer.setFirstName("Arun");
		customer.setLastName("Pawar");
		customer.setMiddleName("Singh");
		customer.setEmail("Arun@gmail.com");
		customer.setActive(false);
		customer.setDeleted(true);
		String pass = passwordEncoder.encode("12345");
		customer.setPassword(pass);
		customer.setContact("3525761182");

		Orders orders = new Orders();
		orders.setAmountPaid(53.4);
		orders.setCustomerAddressAddressLine("ghjsaj");
		orders.setCustomerAddressCity("New Delhi");
		orders.setCustomerAddressLabel("home");
		orders.setCustomerAddressState("Delhi");
		orders.setCustomerAddressZipCode("671");
		orders.setPaymentMethod("cod");
		orders.setCustomerAddressCountry("india");

		//orderRepository.save(orders);
		customer.addOrders(orders);
		userRepository.save(customer);

	}

	@Test
	public void testCategoryProduct()
	{
		Category category = new Category();
		category.setName("Fashion");
		Category shirt = new Category("shirt");
		Category shoes = new Category("shoes");

		Set<Category> shirts = new HashSet<>();
		shirts.add(new Category("Casual"));
		shirts.add(new Category("Formal"));
		shirt.setCategorySet(shirts);

		Set<Category> shoesSet = new HashSet<>();
		shoesSet.add(new Category("sneakers"));
		shoesSet.add(new Category("sports"));
		shoes.setCategorySet(shoesSet);

		Set<Category> categories = new HashSet<>();
		categories.add(shirt);
		categories.add(shoes);
		category.setCategorySet(categories);

		Product product = new Product();
		product.setReturnable(false);
		product.setDescription("hjdshhj");
		product.setName("hdkj");
		product.setCancellable(false);
		product.setBrand("wrong");
		product.setActive(true);

		shirt.addProducts(product);
		categoryRepository.save(category);
	}

	@Test
	public void testProductVariation()
	{
		ProductVariation p = new ProductVariation();
		p.setActive(false);
		p.setPrice(5632.4);
		p.setPrimaryImageName("hello");
		p.setQuantityAvailable(7);
		p.setMetadata("{ \"color\" : \"black\", \"type\" : \"sports\" }");
		productVariationRepository.save(p);
	}

	@Test
	public void testProductReview()
	{
		Customer customer = new Customer();
		customer.setFirstName("Arun");
		customer.setLastName("Pawar");
		customer.setMiddleName("Singh");
		customer.setEmail("Arun@gmail.com");
		customer.setActive(false);
		customer.setDeleted(true);
		String pass = passwordEncoder.encode("12345");
		customer.setPassword(pass);
		customer.setContact("3525761182");

		Product product = new Product();
		product.setActive(true);
		product.setBrand("OnePlus");
		product.setCancellable(true);
		product.setDescription("One plus 8T");
		product.setName("OnePlus mobile");
		product.setReturnable(false);

		//user.setSeller(seller);

		ProductReview review = new ProductReview(product,customer,"great","5");
		customer.addReviews(review);

		userRepository.save(customer);
		productRepository.save(product);
	}
}
