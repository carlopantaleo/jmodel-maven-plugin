<?xml version="1.0" encoding="UTF-8"?>
<!-- &{autogen-warn} -->

<hibernate-mapping xmlns="http://www.hibernate.org/xsd/hibernate-mapping"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.hibernate.org/xsd/hibernate-mapping
        http://www.hibernate.org/xsd/hibernate-mapping/hibernate-mapping-4.0.xsd">

    <class name="&{qualified-class-name}"
           table="&{table-name}">
        @ifdef(single-id)[
        <id name="&{pk-field-name}" column="&{pk-field-column-name}">
            <type name="&{field-column-type}">
                &{type-params}
            </type>
            <!-- Generator not yet supported -->
        </id>
        ]
        @ifdef(composite-id)[
        <composite-id>
            @iterated(pk-field)[
            <key-property name="&{field-name}" column="&{field-column-name}">
                <type name="&{field-column-type}">
                    &{type-params}
                </type>
            </key-property>
            ]
        </composite-id>
        ]

        @iterated(field)[
        <property name="&{field-name}" column="&{field-column-name}">
            <type name="&{field-column-type}">
                &{type-params}
            </type>
        </property>
        ]
    </class>

</hibernate-mapping>