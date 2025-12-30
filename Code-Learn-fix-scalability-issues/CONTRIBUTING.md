# Contributing to Code-Learn

Thank you for your interest in contributing to Code-Learn! This document provides guidelines and instructions for contributing.

## Code of Conduct

Please be respectful and constructive in all interactions. We aim to maintain a welcoming and inclusive community.

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in Issues
2. If not, create a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details (OS, Java version, etc.)
   - Relevant logs or screenshots

### Suggesting Enhancements

1. Check if the enhancement has been suggested
2. Create a new issue with:
   - Clear description of the enhancement
   - Use cases and benefits
   - Possible implementation approach

### Pull Requests

1. **Fork the repository**
   ```bash
   git clone https://github.com/HARSHAVARDHINI-11/Code-Learn.git
   cd Code-Learn
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow the coding standards (see below)
   - Add tests for new functionality
   - Update documentation as needed

4. **Test your changes**
   ```bash
   # Build all services
   mvn clean package
   
   # Run tests
   mvn test
   
   # Test locally with Docker Compose
   docker-compose up
   ```

5. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add new feature description"
   ```
   
   Follow conventional commits format:
   - `feat:` - New feature
   - `fix:` - Bug fix
   - `docs:` - Documentation changes
   - `style:` - Code style changes (formatting, etc.)
   - `refactor:` - Code refactoring
   - `test:` - Adding or updating tests
   - `chore:` - Maintenance tasks

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Provide clear description of changes
   - Reference related issues
   - Ensure all CI checks pass

## Coding Standards

### Java Code Style

- **Indentation**: 4 spaces (no tabs)
- **Line length**: Maximum 120 characters
- **Naming conventions**:
  - Classes: PascalCase (e.g., `UserService`)
  - Methods: camelCase (e.g., `getUserById`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
  - Variables: camelCase (e.g., `userId`)

### Best Practices

1. **Single Responsibility Principle**: Each class should have one responsibility
2. **Dependency Injection**: Use constructor injection
3. **Error Handling**: Use appropriate exception handling
4. **Logging**: Use SLF4J for logging
5. **Comments**: Write self-documenting code, use comments for complex logic
6. **Testing**: Write unit tests for business logic

### Example Code Structure

```java
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    
    @Autowired
    public UserService(UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Transactional
    @CircuitBreaker(name = "userService")
    public User createUser(User user) {
        // Validation
        validateUser(user);
        
        // Business logic
        User savedUser = userRepository.save(user);
        
        // Event publishing
        rabbitTemplate.convertAndSend("user.exchange", "user.created", savedUser);
        
        return savedUser;
    }
    
    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
```

## Project Structure

Each microservice follows this structure:

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/codelearn/service/
│   │   │       ├── ServiceApplication.java
│   │   │       ├── controller/
│   │   │       │   └── ServiceController.java
│   │   │       ├── service/
│   │   │       │   └── ServiceService.java
│   │   │       ├── repository/
│   │   │       │   └── ServiceRepository.java
│   │   │       └── model/
│   │   │           └── Entity.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
│           └── com/codelearn/service/
│               └── ServiceApplicationTests.java
├── Dockerfile
└── pom.xml
```

## Testing Guidelines

### Unit Tests

- Test business logic in service layer
- Mock external dependencies
- Use JUnit 5 and Mockito
- Aim for >80% code coverage

Example:
```java
@SpringBootTest
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User result = userService.createUser(user);
        
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any());
    }
}
```

### Integration Tests

- Test API endpoints
- Use TestContainers for infrastructure
- Test service interactions

## Documentation

- Update README.md for user-facing changes
- Update ARCHITECTURE.md for architectural changes
- Update API documentation for endpoint changes
- Add inline comments for complex logic
- Update DEPLOYMENT.md for deployment changes

## Review Process

1. All PRs require at least one approval
2. All CI checks must pass
3. Code must follow style guidelines
4. Tests must be included for new features
5. Documentation must be updated

## Development Setup

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed setup instructions.

Quick start:
```bash
# Use the dev helper script
./dev-helper.sh

# Or manually:
mvn clean package -DskipTests
docker-compose up -d
```

## Need Help?

- Check existing issues and documentation
- Ask questions in GitHub Discussions
- Reach out to maintainers

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Recognition

Contributors will be acknowledged in our README.md file.

Thank you for contributing to Code-Learn! 🎉
