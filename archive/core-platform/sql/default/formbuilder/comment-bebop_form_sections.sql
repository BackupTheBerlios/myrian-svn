comment on table bebop_form_sections is '
 This table contains some essential attributes particular to a Bebop 
 FormSection. 
';
comment on column bebop_form_sections.action is '
 This is the form HTML attribute action. You might think it should be stored in
 the attribute_string of the bebop_components table, and so do I. However,
 Bebop treats the action as a special case and doesnt store it as the other
 attributes.
';
