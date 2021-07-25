Characters for ${user}

<#list characters as character>
  Name: ${character.name}
  Profession: ${character.profession}
  Race: ${character.race}
  Level: ${character.level}
  Last Login: ${character.lastModified}
  Created On: ${character.created}
  Deaths: ${character.deaths}

</#list>