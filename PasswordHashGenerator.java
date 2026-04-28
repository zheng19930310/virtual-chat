import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "hibaobao";
        String hashed = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hashed);
        System.out.println("\n验证匹配: " + encoder.matches(password, hashed));
    }
}
