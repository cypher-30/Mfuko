"""
One-shot generator for Mfuko's legacy/raster app icon assets.

Why this exists: the adaptive icon (API 26+) is now authored as vector XML
(app/src/main/res/drawable/ic_launcher_foreground.xml +
drawable-night/ic_launcher_foreground.xml + drawable/ic_launcher_monochrome.xml,
wired up in mipmap-anydpi-v26/ic_launcher*.xml). API 24/25 devices ignore that
adaptive XML entirely and fall back to the per-density mipmap-*/ic_launcher*.webp
files, and the Play Store listing needs its own flat 512x512 PNG — neither of
those can be a vector, so this script rasterizes the same coin geometry/palette
used by the vector drawables (see APP_REDESIGN_BRIEF.md /
"Mfuko Visual Redesign.dc.html" §01) directly with Pillow.

Usage:
    python "generate_icons.py"

Run from anywhere — paths are resolved relative to this file's location
(icon and launch animations/, a sibling of app/ at the project root).
"""
from __future__ import annotations

import os
from PIL import Image, ImageDraw

# ── Geometry shared with ic_launcher_foreground.xml — viewBox is 0..108 ────
CX = 54.0
FACE_RX, FACE_RY = 26.0, 7.5
HL_RX, HL_RY = 19.0, 2.8

# (edge_cy, face_cy, hl_cy, edge_color, face_color, hl_color, hl_alpha) — bottom to top.
# Light-mode palette only: legacy/round/Play-Store assets have no dark variant
# (matches the single static set already shipped for these slots).
COINS = [
    (73, 70, 67.5, "#040F08", "#0B3D26", "#0F5132", 0.35),  # coin 4 — bottom, green
    (61, 58, 55.5, "#06200F", "#0F5132", "#146647", 0.40),  # coin 3 — green
    (49, 46, 43.5, "#0A2C22", "#1F8059", "#3FA476", 0.40),  # coin 2 — green
    (37, 34, 31.5, "#7A5F09", "#D4A017", "#EBCB6E", 0.55),  # coin 1 — top, gold harvest
]

BG_LIGHT = "#EAF6EF"
SUPERSAMPLE = 4  # render at 4x then downscale (LANCZOS) for anti-aliased edges

THIS_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(THIS_DIR)
RES_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "res")
PLAYSTORE_PNG = os.path.join(PROJECT_ROOT, "app", "src", "main", "ic_launcher-playstore.png")

# dp baseline for the legacy/round mipmap slots is 48dp; density multipliers below.
DENSITIES = {
    "mipmap-mdpi": 1.0,
    "mipmap-hdpi": 1.5,
    "mipmap-xhdpi": 2.0,
    "mipmap-xxhdpi": 3.0,
    "mipmap-xxxhdpi": 4.0,
}
LEGACY_BASE_DP = 48
FOREGROUND_BASE_DP = 108  # foreground asset is conventionally exported at the full 108dp canvas


def hex_to_rgb(h: str) -> tuple[int, int, int]:
    h = h.lstrip("#")
    return tuple(int(h[i : i + 2], 16) for i in (0, 2, 4))


def draw_ellipse(draw: ImageDraw.ImageDraw, cx: float, cy: float, rx: float, ry: float, color_hex: str, alpha: float = 1.0) -> None:
    r, g, b = hex_to_rgb(color_hex)
    draw.ellipse([cx - rx, cy - ry, cx + rx, cy + ry], fill=(r, g, b, round(alpha * 255)))


def render_foreground(size_px: int) -> Image.Image:
    """Transparent-background coin stack, scaled from the 0..108 viewBox to size_px."""
    ss_size = size_px * SUPERSAMPLE
    scale = ss_size / 108.0
    img = Image.new("RGBA", (ss_size, ss_size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    for edge_cy, face_cy, hl_cy, edge_color, face_color, hl_color, hl_alpha in COINS:
        draw_ellipse(draw, CX * scale, edge_cy * scale, FACE_RX * scale, FACE_RY * scale, edge_color)
        draw_ellipse(draw, CX * scale, face_cy * scale, FACE_RX * scale, FACE_RY * scale, face_color)
        draw_ellipse(draw, CX * scale, hl_cy * scale, HL_RX * scale, HL_RY * scale, hl_color, hl_alpha)
    return img.resize((size_px, size_px), Image.LANCZOS)


def render_full_icon(size_px: int, bg_hex: str) -> Image.Image:
    """Background + foreground composited, full-bleed square, no mask."""
    bg = Image.new("RGBA", (size_px, size_px), hex_to_rgb(bg_hex) + (255,))
    bg.alpha_composite(render_foreground(size_px))
    return bg


def apply_mask(img: Image.Image, mask_img: Image.Image) -> Image.Image:
    out = img.copy()
    out.putalpha(mask_img)
    return out


def circle_mask(size_px: int) -> Image.Image:
    ss_size = size_px * SUPERSAMPLE
    big = Image.new("L", (ss_size, ss_size), 0)
    ImageDraw.Draw(big).ellipse([0, 0, ss_size - 1, ss_size - 1], fill=255)
    return big.resize((size_px, size_px), Image.LANCZOS)


def rounded_rect_mask(size_px: int, radius_frac: float = 0.1875) -> Image.Image:
    ss_size = size_px * SUPERSAMPLE
    big = Image.new("L", (ss_size, ss_size), 0)
    ImageDraw.Draw(big).rounded_rectangle([0, 0, ss_size - 1, ss_size - 1], radius=int(ss_size * radius_frac), fill=255)
    return big.resize((size_px, size_px), Image.LANCZOS)


def save_webp(img: Image.Image, path: str) -> None:
    """Save as WEBP; if the local Pillow build lacks WEBP support, fall back to
    a same-basename PNG and remove the stale .webp so the two resources don't
    collide (Android resource names are extension-independent)."""
    try:
        img.save(path, "WEBP", lossless=True)
    except Exception as exc:  # pragma: no cover - environment-dependent
        png_path = os.path.splitext(path)[0] + ".png"
        img.save(png_path, "PNG")
        if os.path.exists(path):
            os.remove(path)
        print(f"  ! WEBP save failed ({exc}); wrote PNG fallback instead: {png_path}")


def main() -> None:
    for folder, density_mult in DENSITIES.items():
        legacy_size = round(LEGACY_BASE_DP * density_mult)
        fg_size = round(FOREGROUND_BASE_DP * density_mult)
        out_dir = os.path.join(RES_DIR, folder)

        full_icon = render_full_icon(legacy_size, BG_LIGHT)
        legacy_icon = apply_mask(full_icon, rounded_rect_mask(legacy_size))
        save_webp(legacy_icon, os.path.join(out_dir, "ic_launcher.webp"))

        round_icon = apply_mask(full_icon, circle_mask(legacy_size))
        save_webp(round_icon, os.path.join(out_dir, "ic_launcher_round.webp"))

        foreground_only = render_foreground(fg_size)
        save_webp(foreground_only, os.path.join(out_dir, "ic_launcher_foreground.webp"))

        print(f"{folder}: legacy={legacy_size}px round={legacy_size}px foreground={fg_size}px")

    playstore_icon = render_full_icon(512, BG_LIGHT).convert("RGB")  # Play Store requires no alpha
    playstore_icon.save(PLAYSTORE_PNG, "PNG")
    print(f"Play Store icon: 512px -> {PLAYSTORE_PNG}")


if __name__ == "__main__":
    main()
