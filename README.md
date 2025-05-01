<div align="center">
  <img src="./Assets/name.png">
</div>

#### <div align="center">**Reimagining Furniture Shopping with AR - Built Natively in Android👀**
</div>

The core idea behind Unreal Furniture👀 is to transform the traditional online furniture shopping experience by allowing customers to visualize😎 how different furnitures would look in their own home spaces by leveraging Augmented Reality (AR) and 3D visualization technology🤖. It is one of the most comprehensive and feature-rich android application📱 that I have developed using Kotlin, XML Layouts, ARCore, and Firebase as a part of my final year project🎓. In addition to AR-based placement and 3D visualization, the app offers advanced features like filtering products by category, price, and availability✅, adding items to wishlist or directly to the cart, real-time order tracking notifications, detailed product reviews, and an admin dashboard for managing stock, insights📊, and delivery statuses. Unreal Furniture holds the vision of revolutionizing the way people shop for furniture🛋️, making it more interactive, personalized, and efficient bringing customers closer to their dream home furnishings💫

PS - As Unreal Furniture! being one of my early deep-dive Android Projects🌱, it may not reflect all best practices⚙️ but it has been a greate learning experience where it taught me a lot about real-world android app development✨ where every feature added and bug fixed is a learning experience😄

-----

### **// App Preview 😎**

Splash Screen |  Signup Screen | Signin Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS1.jpg" width=280 />  |  <img src="/screenshots/SS2.jpg" width=280 /> | <img src="/screenshots/SS3.jpg" width=280 />

User Home Screen |  Products Screen | Sort Products Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS4.jpg" width=280 />  |  <img src="/screenshots/SS5.jpg" width=280 /> | <img src="/screenshots/SS6.jpg" width=280 />

Filter Products Screen |  Product Details Screen | Product 3D Model Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS7.jpg" width=280 />  |  <img src="/screenshots/SS8.jpg" width=280 /> | <img src="/screenshots/SS9.jpg" width=280 />

User Profile Screen |  Add Address Screen | User Addresses Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS10.jpg" width=280 />  |  <img src="/screenshots/SS11.jpg" width=280 /> | <img src="/screenshots/SS12.jpg" width=280 />

Wishlist Screen |  Cart Screen | Checkout Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS13.jpg" width=280 />  |  <img src="/screenshots/SS14.jpg" width=280 /> | <img src="/screenshots/SS15.jpg" width=280 />

Order Placed Screen |  Real-time Notification | User Orders Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS16.jpg" width=280 />  |  <img src="/screenshots/SS17.jpg" width=280 /> | <img src="/screenshots/SS18.jpg" width=280 />

Order Details Screen |  Admin Home Screen | Add Product Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS19.jpg" width=280 />  |  <img src="/screenshots/SS20.jpg" width=280 /> | <img src="/screenshots/SS21.jpg" width=280 />

Update Product Screen |  Admin Orders Screen | Received Order Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS22.jpg" width=280 />  |  <img src="/screenshots/SS23.jpg" width=280 /> | <img src="/screenshots/SS24.jpg" width=280 />

Contact-us Screen |  Product AR Screen | Offline Screen
:-------------------------:|:-------------------------: | :-------------------------: 
<img src="/screenshots/SS25.jpg" width=280 />  |  <img src="/screenshots/SS26.jpg" width=280 /> | <img src="/screenshots/SS27.jpg" width=280 />

---

### **// Features⚡**

- **Authentication**: Sign-up, sign-in and reset-password using Firebase Authentication with Email/Password or Google Sign-In.
- **Database**: Storing user data in Firebase Firestore and Storage for real-time updates and secure data retrieval.
- **Home Screen**: Users can view all available products, including prices, discounts, and availability.
- **Product Search**: Users can efficiently search for products using keywords, categories, and filters.
- **Shimmer Effect**: Provides a placeholder effect while loading data, enhancing user experience.
- **Lazy Loading**: Efficiently loads images and data using lazy loading techniques, improving performance and reducing memory usage.
- **View Product Details**: Users can view detailed information about products, including images, descriptions, AR viewing, 3D model, and reviews.
- **User Profile**: Users can manage their profiles, including adding profile picture, addresses and contact information.
- **User Orders**: Users can view their order history, including order details, status, and tracking information.
- **Wishlist**: Users can add products to their wishlist for future reference and easy access.
- **Add to Cart**: Users can add products to their cart, view cart items, and proceed to checkout.
- **Checkout**: Users can place orders, select delivery addresses, and make payments using various payment methods.
- **Admin Dashboard**: Admin can add new products, manage exisiting products, including product updates, stocks, orders/payments received, etc.
- **Real-Time Notifications**: Users/Admin receive real-time notifications for order status updates, payments, and reviews.
- **Ratings & Reviews**: Users can rate and review products, helping other users make informed decisions.
- **AR & 3D Model**: Users can view products in Augmented Reality (AR) and 3D model, allowing them to visualize how the product would look in their space.
- **User-Friendly UI**: The app is designed with a clean and modern user interface, providing an intuitive and seamless experience.

---

### **// XML Layout UI Components 🪨**

- **ConstraintLayout**: Used for building responsive, flexible, and flat hierarchy layouts.
- **RecyclerView**: Efficiently displays dynamic lists data using ViewHolder pattern.
- **CardView**: Displays information in a card format, providing a clean and modern look.
- **TextView**: Displays text in various styles, formats and with marquee effect.
- **TextInputLayout**: Provides a floating label for text input fields, enhancing user experience.
- **ImageView**: Displays images in various formats, sizes and shapes.
- **Buttons**: Displays clickable buttons for user actions, with various styles and effects.
- **WebView**: Displays web content within the app, allowing users to view external links and resources.
- **ARCore**: Displays Augmented Reality content using ARCore, allowing users to visualize products in their space.
- **SkeletonLayout**: Provides a placeholder effect while loading data, enhancing user experience.
- **ARFragment**: Displays Augmented Reality content using ARCore, allowing users to visualize products in their space.
- **SearchView**: Provides a search bar for users to search for products and categories.
- **BottomNavigationView**: Provides a bottom navigation bar for easy access to different app sections.
- **DialogBuilder**: Displays custom dialogs for user interactions, confirmations, and alerts.
- **BottomSheetDialog**: Displays a bottom sheet for additional options or information without leaving the current screen.

---

### **// Libraries and Dependencies 🤖**

- **Material Components**: Provides Material Design components for building beautiful and responsive UIs.
- **Coroutines**: Kotlin library for asynchronous programming, used for handling background tasks and network requests.
- **OkHttp**: HTTP client for Android, used for making network requests and handling responses.
- **ReadmoreText**: Library for displaying long text with a "Read More" feature, allowing users to expand and collapse text.
- **Image Picker**: Library for selecting images from the device's gallery or camera.
- **Picasso**: Image loading and caching library for Android, used for displaying images efficiently.
- **Glide**: Image loading and caching library for Android, used for displaying images efficiently.
- **ARCore**: Google's platform for building Augmented Reality experiences, used for displaying AR content.
- **SkeletonLayout**: Library for creating skeleton/shimmer loading effects, enhancing user experience during data loading.
- **ExpressView**: Library for displaying animated heart icons for add to wishlist actions.
- **Firebase Auth Library**: Official Firebase library for authentication services like sign-in, sign-up, email-verification and password reset.   
- **Firebase Firestore**: Official Firebase library for real-time database services, including data storage and retrieval.
- **Firebase Storage**: Official Firebase library for storing and retrieving files, including images and documents.
- **Firebase Cloud Messaging**: Official Firebase library for sending and receiving real-time notifications.
- **Notify**: Library for creating and managing notifications in Android apps.
- **MPAndroidChart**: Library for creating beautiful and interactive charts and graphs in Android apps.
- **Lottie**: Library for rendering animations in Android apps, used for creating beautiful and engaging UIs.

---

### **// Installation 📲**

- Clone the repository using the following command:
  ```bash
  // Clone the repository
  git clone https://github.com/v1kasjaiswal/UnrealFurniture.git

  // Change the directory
  cd ./UnrealFurniture/UnrealFurniture

  // Make Sure to Add the google-services.json file in the app directory
  ```  
- Assets folder contains all the images, descriptions and AR/3D models used in the project.
- You can add all these products to your firebase through Admin Dashboard.
- Open the project in Android Studio and build the project.
- Run the project on an emulator or physical device.

---

### **// License 📜**

This project is licensed under the MIT License - see the [LICENSE](/LICENSE) file for details.

---

### **// Acknowledgements 🤝**

- **[Sketchfab](https://sketchfab.com/)** for providing free and beautiful Furniture AR/3D models.
- **[LottieFiles](https://github.com/airbnb/lottie-android)** for providing free and beautiful lottie animations.
- **[ARCore](https://developers.google.com/ar)** for providing a powerful and easy-to-use Augmented Reality platform for Android apps.
- **[MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)** for providing awesome and interactive charts and bar graphs.
- **[Picasso](https://square.github.io/picasso/)** for providing a powerful image loading and caching library.
- **[Glide](https://github.com/bumptech/glide)** for providing a powerful image loading and caching library.
- **[OkHttp](https://square.github.io/okhttp/)** for providing a powerful HTTP client for Android.
- **[ImagePicker](https://github.com/Dhaval2404/ImagePicker)** for providing a powerful image picker library.
- **[SkeletonLayout](https://github.com/Faltenreich/SkeletonLayout)** for providing a powerful skeleton/shimmer loading effect library.
- **[ExpressView](https://github.com/ankurg22/ExpressView)** for providing a powerful animated heart icon library.
- **[ReadmoreTextView](https://github.com/colourmoon/readmore-textview)** for providing a powerful read more text view library.

---

### **// Disclaimer ⚠️**

🚧 Unreal Furniture was developed in the year 2024 and as of now in 2025, there are several changes and deprecations are being made(on August 2025) in various third-party libraries and dependencies used in this project(Firebase Specially). The project may not work as expected due to these changes.

---

### **// Support 💖**

If you like this project, please consider giving it a ⭐. It will help me grow and improve. Thanks! 🚀

---

<div align="center">
Made with 💖 by Vikas Jaiswal (ValiantX)
</div>
