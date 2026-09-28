/* =========================================================
   INTRO SYSTEM PANEL - LARGE / CINEMATIC UPGRADE
========================================================= */

/* 오른쪽 패널 전체 크기 */
.system-panel {
  position: relative;

  width: min(31vw, 500px);
  min-width: 440px;

  padding: 36px 38px;

  overflow: hidden;

  background:
    linear-gradient(
      145deg,
      rgba(12, 48, 84, 0.82),
      rgba(5, 27, 54, 0.68)
    );

  border:
    1px solid rgba(123, 190, 255, 0.24);

  border-radius: 22px;

  backdrop-filter: blur(22px);

  box-shadow:
    0 35px 90px rgba(0, 0, 0, 0.32),
    0 0 70px rgba(32, 128, 255, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);

  transition:
    transform 0.35s ease,
    border-color 0.35s ease,
    box-shadow 0.35s ease;
}


/* 카드 뒤 은은한 블루 빛 */
.system-panel::before {
  position: absolute;

  top: -150px;
  right: -130px;

  width: 330px;
  height: 330px;

  background:
    radial-gradient(
      circle,
      rgba(53, 151, 255, 0.19),
      transparent 68%
    );

  content: '';

  pointer-events: none;

  animation: panelGlow 5s ease-in-out infinite alternate;
}


/* 카드 왼쪽 상단 얇은 포인트 */
.system-panel::after {
  position: absolute;

  top: 0;
  left: 38px;

  width: 80px;
  height: 2px;

  background:
    linear-gradient(
      90deg,
      #45a7ff,
      rgba(69, 167, 255, 0)
    );

  content: '';
}


/* 마우스 올렸을 때 살짝 떠오름 */
.system-panel:hover {
  transform: translateY(-6px);

  border-color:
    rgba(123, 190, 255, 0.38);

  box-shadow:
    0 45px 110px rgba(0, 0, 0, 0.38),
    0 0 90px rgba(32, 128, 255, 0.13),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}


/* =========================================================
   PANEL HEADER
========================================================= */

.panel-header {
  position: relative;
  z-index: 2;

  display: flex;
  align-items: center;
  justify-content: space-between;
}


.panel-header > div {
  gap: 7px;
}


.panel-small {
  color: #6db9ff;

  font-size: 11px;
  font-weight: 900;

  letter-spacing: 2.4px;
}


.panel-header strong {
  color: #ffffff;

  font-size: 23px;
  font-weight: 900;

  letter-spacing: -0.6px;
}


/* =========================================================
   LIVE
========================================================= */

.live {
  padding: 8px 11px;

  background:
    rgba(49, 217, 139, 0.08);

  border:
    1px solid rgba(49, 217, 139, 0.13);

  border-radius: 20px;

  color: #67e9aa;

  font-size: 10px;
  font-weight: 900;

  letter-spacing: 1.2px;
}


.live-dot {
  width: 8px;
  height: 8px;

  box-shadow:
    0 0 0 5px rgba(53, 221, 145, 0.1),
    0 0 18px rgba(53, 221, 145, 0.7);

  animation:
    livePulse 1.7s ease-in-out infinite;
}


/* =========================================================
   DIVIDER
========================================================= */

.panel-divider {
  margin: 28px 0 8px;

  background:
    linear-gradient(
      90deg,
      rgba(255, 255, 255, 0.16),
      rgba(255, 255, 255, 0.04)
    );
}


/* =========================================================
   SYSTEM ROW
========================================================= */

.system-row {
  position: relative;

  z-index: 2;

  grid-template-columns:
    48px minmax(0, 1fr) auto;

  gap: 17px;

  padding: 23px 0;

  opacity: 0;

  transform: translateX(28px);

  animation:
    systemRowEnter 0.7s
    cubic-bezier(.2, .8, .2, 1)
    forwards;

  transition:
    padding 0.25s ease,
    background 0.25s ease;
}


/* 01 */
.system-row:nth-of-type(3) {
  animation-delay: 1.35s;
}


/* 02 */
.system-row:nth-of-type(4) {
  animation-delay: 1.55s;
}


/* 03 */
.system-row:nth-of-type(5) {
  animation-delay: 1.75s;
}


.system-row:hover {
  padding-right: 12px;
  padding-left: 12px;

  background:
    linear-gradient(
      90deg,
      rgba(44, 137, 237, 0.08),
      transparent
    );
}


/* =========================================================
   NUMBER
========================================================= */

.system-icon {
  width: 46px;
  height: 46px;

  background:
    linear-gradient(
      145deg,
      rgba(43, 139, 245, 0.18),
      rgba(43, 139, 245, 0.07)
    );

  border:
    1px solid rgba(88, 170, 255, 0.28);

  border-radius: 11px;

  color: #75beff;

  font-size: 12px;
  font-weight: 900;

  box-shadow:
    inset 0 0 20px rgba(42, 140, 246, 0.04);

  transition:
    0.25s ease;
}


.system-row:hover .system-icon {
  border-color:
    rgba(91, 177, 255, 0.55);

  background:
    rgba(44, 139, 244, 0.22);

  transform:
    translateY(-2px)
    scale(1.04);
}


/* =========================================================
   TEXT
========================================================= */

.system-text {
  gap: 5px;
}


.system-text span {
  color:
    rgba(205, 222, 240, 0.48);

  font-size: 10px;

  letter-spacing: 0.15px;
}


.system-text strong {
  color:
    rgba(255, 255, 255, 0.94);

  font-size: 14px;
  font-weight: 800;

  letter-spacing: -0.2px;
}


/* =========================================================
   STATUS
========================================================= */

.system-status {
  position: relative;

  padding-left: 12px;

  color: #62e6a5;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 1.1px;
}


.system-status::before {
  position: absolute;

  top: 50%;
  left: 0;

  width: 5px;
  height: 5px;

  background: #43dc96;

  border-radius: 50%;

  content: '';

  transform: translateY(-50%);

  box-shadow:
    0 0 9px rgba(67, 220, 150, 0.65);
}


/* =========================================================
   PANEL ENTER
========================================================= */

.animation-panel {
  opacity: 0;

  transform:
    translateX(85px)
    translateY(20px)
    scale(0.92);

  filter: blur(7px);

  animation:
    panelCinematicEnter
    1.15s
    cubic-bezier(.16, 1, .3, 1)
    0.82s
    forwards;
}


/* =========================================================
   KEYFRAMES
========================================================= */

@keyframes panelCinematicEnter {

  0% {
    opacity: 0;

    filter: blur(7px);

    transform:
      translateX(85px)
      translateY(20px)
      scale(0.92);
  }

  65% {
    opacity: 1;

    filter: blur(0);

    transform:
      translateX(-6px)
      translateY(0)
      scale(1.01);
  }

  100% {
    opacity: 1;

    filter: blur(0);

    transform:
      translateX(0)
      translateY(0)
      scale(1);
  }
}


@keyframes systemRowEnter {

  from {
    opacity: 0;

    transform:
      translateX(28px);
  }

  to {
    opacity: 1;

    transform:
      translateX(0);
  }
}


@keyframes livePulse {

  0%,
  100% {
    opacity: 1;

    box-shadow:
      0 0 0 4px rgba(53, 221, 145, 0.12),
      0 0 13px rgba(53, 221, 145, 0.65);
  }

  50% {
    opacity: 0.55;

    box-shadow:
      0 0 0 8px rgba(53, 221, 145, 0.03),
      0 0 25px rgba(53, 221, 145, 0.85);
  }
}


@keyframes panelGlow {

  from {
    opacity: 0.45;

    transform:
      scale(0.92);
  }

  to {
    opacity: 1;

    transform:
      scale(1.12);
  }
}


/* =========================================================
   LARGE SCREEN
========================================================= */

@media (min-width: 1500px) {

  .system-panel {
    width: 510px;

    padding: 40px 42px;
  }

  .panel-header strong {
    font-size: 25px;
  }

  .system-row {
    padding: 25px 0;
  }

}


/* =========================================================
   TABLET
========================================================= */

@media (max-width: 1200px) {

  .system-panel {
    width: 400px;
    min-width: 400px;

    padding: 30px;
  }

}


/* =========================================================
   MOBILE
========================================================= */

@media (max-width: 900px) {

  .system-panel {
    display: none;
  }

}
