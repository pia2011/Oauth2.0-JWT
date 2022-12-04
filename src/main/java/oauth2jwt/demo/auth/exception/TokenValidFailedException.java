package oauth2jwt.demo.auth.exception;

public class TokenValidFailedException extends RuntimeException{
    public TokenValidFailedException(){
        super("토큰을 생성하는데 실패했습니다.");
    }
    public TokenValidFailedException(String msg){
        super(msg);
    }
}
