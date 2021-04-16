package apicross.demo.common.models;

import apicross.demo.common.utils.HasETag;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity implements HasETag {
    @Id
    private String id;
    @Version
    private long version;

    public AbstractEntity(String id, long version) {
        this.id = Objects.requireNonNull(id);
        this.version = version;
    }

    protected AbstractEntity() {
    }

    public String getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public String etag() {
        return DigestUtils.md5Hex(id + Long.toHexString(10 * version));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
