package pl.mmorpg.prototype.server.database.entities;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.mmorpg.prototype.server.database.entities.jointables.CharactersQuests;

@Entity(name = "UserCharacter")
@Table(name = "User_Characters")
@Data
@EqualsAndHashCode(of="id")
public class UserCharacter implements Serializable
{
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "hit_points", nullable = false)
    private Integer hitPoints = 100;

    @Column(name = "mana_points")
    private Integer manaPoints = 100;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "level", nullable = false)
    private Integer level = 1;

    @Column(name = "experience", nullable = false)
    private Integer experience = 0;

    @Column(name = "strength", nullable = false)
    private Integer strength = 5;

    @Column(name = "magic", nullable = false)
    private Integer magic = 5;

    @Column(name = "dexitirity", nullable = false)
    private Integer dexitirity = 5;

    @Column(name = "gold", nullable = false)
    private Integer gold = 100;

    @Column(name = "last_location_x")
    private Integer lastLocationX = 96;

    @Column(name = "last_location_y")
    private Integer lastLocationY = 96;

    @OneToMany(mappedBy = "character")
    @MapKey(name = "fieldPosition")
    private Map<Integer, QuickAccessBarConfigurationElement> quickAccessBarConfig;

    @OneToMany(mappedBy = "key.character", cascade=CascadeType.ALL)
    private Set<CharactersQuests> quests;

}
