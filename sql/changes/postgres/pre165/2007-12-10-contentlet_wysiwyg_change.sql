alter table contentlet add column disabled_wysiwyg VARCHAR(255) DEFAULT NULL;

update contentlet set disabled_wysiwyg = enabled_wysiwyg;

alter table contentlet drop column enabled_wysiwyg;