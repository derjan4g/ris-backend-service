CREATE TABLE IF NOT EXISTS doc_unit (
    id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    creationtimestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    fileuploadtimestamp TIMESTAMP WITH TIME ZONE,
    s3path VARCHAR(255),
    filetype VARCHAR(30),
    filename VARCHAR(255),
    aktenzeichen VARCHAR(255),
    gerichtstyp VARCHAR(255),
    dokumenttyp VARCHAR(255),
    vorgang VARCHAR(255),
    ecli VARCHAR(255),
    spruchkoerper VARCHAR(255),
    entscheidungsdatum VARCHAR(255),
    gerichtssitz VARCHAR(255),
    rechtskraft VARCHAR(255),
    eingangsart VARCHAR(255),
    dokumentationsstelle VARCHAR(255),
    region VARCHAR(255),
    tenor VARCHAR(255),
    gruende VARCHAR(255),
    tatbestand VARCHAR(255),
    entscheidungsgruende VARCHAR(255),
    abweichendemeinung VARCHAR(255),
    sonstigerlangtext VARCHAR(255),
    gliederung VARCHAR(255),
    berichtigung VARCHAR(255)
);
