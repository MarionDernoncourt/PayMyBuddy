INSERT INTO users (username, email, password) 
VALUES
('Harry', 'hpotter@gryffondor.com', '$2a$10$zH8Dsqt/LrBaFyOZd.3Xw.8FxYzig39Wi6e6K687sW/6ZYFOPHOVK'),
('Hermione', 'hermione@gryffondor.com', '$2a$10$QSylrt0c2rm3Jurc8sL3x.KMP9YuN66zYak8cp/.La.0weUQ/stie'),
('Ron', 'ron@gryffondor.com', '$2a$10$Mh1.KBbdovabqd5jTfI62O20XBjfmNHCidZD1qfs2ajpJyn4PZp/q'),
('Drago', 'dmalefoy@serpentar.com', '$2a$10$BNGIyTpqJrLHrfU/ovJr.OSR5zlvCV3.Spaqn86/B1V7RIreFxq/O'),
('McGonagall', 'mcgonagall@poudlard.com', '$2a$10$ds/ag2P0Rc2Tu5BT8/4Pc.q44lfezSGkhaXIiY791mn1ET2nqzioW'),
('Hagrid', 'hagrid@poudlard.com', '$2a$10$qW6K8BKAiKm7giNfU3ESyeAkQs6/YfWpRIJ4Ro40thz.efev806kS'), 
('Neuville', 'nlondubat@gryffondor.com', '$2a$10$JZBXviN835r4bhmLfF2ESexy3GFhRvsKkxFzY8stHjT4Xa3Qgpnj6');

INSERT INTO user_user (user_id, connected_user_id) 
VALUES 
(1,2), (2,1),(3,1), (1,3),(3,2), (2,3),(4,1), (1,4),(5,1), (1,5),(5,2), (2,5),(5,6), (6,5),(6,1), (1,6),(6,2), (2,6),
(6,3), (3,6),(7,1), (1,7),(7,2), (2,7),(7,3), (3,7);

INSERT INTO transactions (description, amount, sender_id, receiver_id)
VALUES 
('nouvelle baguette magique', 75, 1,2),
('pour tes lunettes', 40, 4, 1),
('vive les mariés', 150, 5, 1),
('HP & Ginny', 100, 7, 1),
('bon mariage', 125, 6, 1),
('bieraubeurre de samedi', 12.55, 3, 7),
('joyeux anniversaire', 75, 2, 3),
('HB', 50, 7, 3), 
('pour ton nimbus 3000 !', 175, 1, 3);
