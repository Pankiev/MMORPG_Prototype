package pl.mmorpg.prototype.server.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import pl.mmorpg.prototype.clientservercommon.ItemTypes;

@Entity(name = "CharacterItem")
@Table(name = "Character_Items")
@Data
public class CharacterItem
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private ItemTypes type;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToOne
	private UserCharacter character;
}
