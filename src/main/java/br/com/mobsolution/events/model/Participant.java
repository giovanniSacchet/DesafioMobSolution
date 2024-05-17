package br.com.mobsolution.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "participants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String cpf;

    @Column(name = "event_id")
    private Long eventId;

    public boolean isValid() {
        return StringUtils.isNotBlank(this.name)
                && StringUtils.isNotBlank(this.cpf)
                && StringUtils.isNotBlank(this.email);
    }

}
