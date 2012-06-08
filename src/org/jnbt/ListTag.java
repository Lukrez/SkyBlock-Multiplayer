package org.jnbt;

import java.util.Collections;
import java.util.List;

public final class ListTag extends Tag
{
  private final Class<? extends Tag> type;
  private final List<Tag> value;

  public ListTag(String name, Class<? extends Tag> type, List<Tag> value)
  {
    super(name);
    this.type = type;
    this.value = Collections.unmodifiableList(value);
  }

  public Class<? extends Tag> getType()
  {
    return this.type;
  }

  public List<Tag> getValue()
  {
    return this.value;
  }

  public String toString()
  {
    String name = getName();
    String append = "";
    if ((name != null) && (!name.equals(""))) {
      append = "(\"" + getName() + "\")";
    }
    StringBuilder bldr = new StringBuilder();
    bldr.append("TAG_List" + append + ": " + this.value.size() + " entries of type " + NBTUtils.getTypeName(this.type) + "\r\n{\r\n");
    for (Tag t : this.value) {
      bldr.append("   " + t.toString().replaceAll("\r\n", "\r\n   ") + "\r\n");
    }
    bldr.append("}");
    return bldr.toString();
  }
}
