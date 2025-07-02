# GroceMart Online Shopping System 🛒

A comprehensive desktop-based e-commerce application for online grocery shopping, built with pure Java technologies.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white)

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Security Features](#security-features)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## 🎯 Overview

GroceMart is a full-featured desktop e-commerce application that provides a complete online grocery shopping experience. The system features separate interfaces for customers and administrators, with comprehensive functionality for product management, order processing, and user account management.

**Key Highlights:**
- Pure Java desktop application with Swing GUI
- Integrated Oracle database connectivity
- Role-based access control
- Real-time inventory management
- Secure authentication system

## ✨ Features

### 👥 Customer Features
- **🔐 User Authentication**: Secure registration and login system
- **🔍 Product Browsing**: Advanced search with category filters
- **🛒 Shopping Cart**: Real-time cart management and updates
- **📦 Order Management**: Complete order processing and history tracking
- **📊 Stock Validation**: Real-time stock availability checking

### 👨‍💼 Administrator Features
- **📋 Inventory Management**: Full CRUD operations for products
- **📊 Order Processing**: Comprehensive order management dashboard
- **👤 User Management**: Customer account administration
- **📈 Sales Analytics**: Reporting and sales tracking
- **⚠️ Stock Monitoring**: Low stock alerts and inventory tracking

## 🔧 Technology Stack

| Component | Technology |
|-----------|------------|
| **Frontend** | Java Swing GUI Components |
| **Backend** | Pure Java |
| **Database** | Oracle Database |
| **Connectivity** | JDBC |
| **IDE** | Eclipse |
| **Architecture** | Desktop Application with Integrated Data Layer |

## 💻 System Requirements

- **Java**: JDK 8 or higher
- **Database**: Oracle Database 11g or higher
- **Memory**: Minimum 4GB RAM
- **Storage**: At least 500MB free space
- **OS**: Windows 10/11, macOS, or Linux

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/grocemart-shopping-system.git
cd grocemart-shopping-system
```

### 2. Database Setup
1. Install Oracle Database
2. Create a new database instance
3. Run the database scripts located in `/database/` folder:
   ```sql
   -- Run these scripts in order
   @create_tables.sql
   @insert_sample_data.sql
   ```

### 3. Configure Database Connection
Update the database configuration in `src/config/DatabaseConfig.java`:
```java
public class DatabaseConfig {
    public static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    public static final String DB_USER = "your_username";
    public static final String DB_PASSWORD = "your_password";
}
```

### 4. Compile and Run
```bash
# Compile the project
javac -cp ".:lib/*" src/**/*.java

# Run the application
java -cp ".:lib/*:src" main.GroceMartMain
```

### 5. Default Login Credentials
**Administrator:**
- Username: `admin`
- Password: `admin123`

**Customer (Demo):**
- Username: `customer1`
- Password: `pass123`

## 🗄️ Database Configuration

### Required Tables
The application requires the following Oracle database tables:
- `users` - User authentication and profile information
- `products` - Product catalog and inventory
- `categories` - Product categories
- `orders` - Order information
- `order_items` - Order line items
- `shopping_cart` - Shopping cart items

### JDBC Configuration
Ensure Oracle JDBC driver is included in your classpath:
```
lib/ojdbc8.jar
```

## 🎮 Usage

### For Customers:
1. **Registration/Login**: Create account or login with existing credentials
2. **Browse Products**: Use search and category filters to find products
3. **Shopping Cart**: Add items to cart and manage quantities
4. **Checkout**: Complete order with shipping details
5. **Order History**: View past orders and track status

### For Administrators:
1. **Login**: Use admin credentials to access admin panel
2. **Manage Products**: Add, edit, or remove products from inventory
3. **Process Orders**: View and update order statuses
4. **User Management**: Manage customer accounts
5. **Reports**: Generate sales and inventory reports


## 🔒 Security Features

- **🛡️ Role-Based Access Control**: Separate customer and admin interfaces
- **💉 SQL Injection Prevention**: PreparedStatements for all database queries
- **🔐 Secure Authentication**: Password hashing and validation
- **✅ Input Validation**: Comprehensive data sanitization
- **🔄 Transaction Integrity**: Database transaction management

## 📸 Screenshots

*Add screenshots of your application here showing:*
- Login screen
- ![Screenshot 2025-06-25 230742](https://github.com/user-attachments/assets/0844589a-e5eb-4060-8355-15fea6fc4d75)

- Product browsing interface![Screenshot 2025-06-25 231406](https://github.com/user-attachments/assets/815bc185-6b4d-42df-bb3f-02ff9bbe88e6)

- Shopping cart![Screenshot 2025-06-25 232022](https://github.com/user-attachments/assets/85a98e74-d0cb-471b-9589-89f87f17814c)

- Admin panel![Screenshot 2025-06-25 233653](https://github.com/user-attachments/assets/03a63b97-7a10-486b-b915-9ddf8241d8d3)

- Order management![Screenshot 2025-06-25 233917](https://github.com/user-attachments/assets/e22bb9ae-ae07-49e1-a2da-2e280de90c7e)


## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎓 Academic Context

This project was developed as part of the Advanced Java Programming course at **Puducherry Technological University**, demonstrating practical application of:
- Object-Oriented Programming principles
- Desktop application development with Java Swing
- Database design and JDBC connectivity
- Software engineering best practices

## 📞 Contact

**Developer**: [Mohan]
- **Email**: mohanasokan412@gmail.com
- **LinkedIn**: [LinkedIn Profile](https://www.linkedin.com/in/mohan-a-4a0077328)
- **University**: Puducherry Technological University

---

⭐ **If you found this project helpful, please give it a star!** ⭐

*Feel free to reach out if you have any questions about the technical implementation or would like to discuss the project further!*
