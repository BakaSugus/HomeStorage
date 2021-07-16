package cn.j.netstorage.Entity.oss;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.tool.EncryUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "t_oss")
public class Oss {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private User user;

    @Column
    private String SecretId;

    @Column
    private String SecretKey;

    @Column
    private String Region;

    @Column
    private String backupBucketName;

    public Oss encrypt() {
        if (this.user == null) return null;
        String salt = this.user.getEmailAccount();
        String id = EncryUtil.encrypt(this.getSecretId(), salt);
        String key = EncryUtil.encrypt(this.getSecretKey(), salt);
        this.setSecretKey(key);
        this.setSecretId(id);
        return this;
    }

    public Oss decrypt() {
        if (this.user == null || !StringUtils.hasText(this.getSecretId()) || !StringUtils.hasText(this.getSecretKey()))
            return null;
        String salt = this.user.getEmailAccount();
        String id = EncryUtil.decrypt(this.getSecretId(), salt);
        String key = EncryUtil.decrypt(this.getSecretKey(), salt);
        this.setSecretKey(key);
        this.setSecretId(id);
        return this;
    }
}
