"""
Generates 256x256 WebP leader thumbnails from full leader card images.

Crop formula: upper-center face region of the card
  left   = w * 0.25
  top    = h * 0.02
  right  = w * 0.75
  bottom = h * 0.45

Source images are PNG content stored with .webp extension (downloaded from
en.onepiece-cardgame.com/images/cardlist/card/{id}.png).

Usage:
  python make_thumbnails.py                  # process all images missing a thumbnail
  python make_thumbnails.py OP01-001         # process a single card ID
"""

import os
import sys
from PIL import Image

IMG_DIR   = r"C:\Users\ahmad\AndroidStudioProjects\Labophase\app\src\main\assets\leader_images"
THUMB_DIR = r"C:\Users\ahmad\AndroidStudioProjects\Labophase\app\src\main\assets\leader_thumbnails"
SIZE      = 256
QUALITY   = 90


def make_thumbnail(card_id: str) -> bool:
    src = os.path.join(IMG_DIR, f"{card_id}.webp")
    dst = os.path.join(THUMB_DIR, f"{card_id}.webp")

    if not os.path.exists(src):
        print(f"SKIP  {card_id} (no source image)")
        return False

    img = Image.open(src).convert("RGB")
    w, h = img.size

    left   = int(w * 0.25)
    top    = int(h * 0.14)
    right  = int(w * 0.75)
    bottom = int(h * 0.56)

    crop = img.crop((left, top, right, bottom))
    thumb = crop.resize((SIZE, SIZE), Image.LANCZOS)
    thumb.save(dst, "WEBP", quality=QUALITY)
    print(f"OK    {card_id}")
    return True


def main():
    if len(sys.argv) > 1:
        card_ids = sys.argv[1:]
    else:
        img_ids   = {os.path.splitext(f)[0] for f in os.listdir(IMG_DIR)}
        thumb_ids = {os.path.splitext(f)[0] for f in os.listdir(THUMB_DIR)}
        card_ids  = sorted(img_ids - thumb_ids)
        print(f"Found {len(card_ids)} images without thumbnails")

    ok = sum(1 for cid in card_ids if make_thumbnail(cid))
    print(f"\nDone: {ok}/{len(card_ids)} created")


if __name__ == "__main__":
    main()
